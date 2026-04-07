package com.ty.logic.system.service.impl;

import com.google.common.collect.Lists;
import com.ty.api.device.service.VideoDeviceService;
import com.ty.api.model.device.VideoDevice;
import com.ty.api.model.system.Organization;
import com.ty.api.model.system.SysUserOrganization;
import com.ty.api.model.system.ThirdAppConfig;
import com.ty.api.system.service.OrganizationService;
import com.ty.api.system.service.SysUserOrganizationService;
import com.ty.api.system.service.ThirdAppConfigService;
import com.ty.cm.constant.enums.CacheKey;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.cache.Cache;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.OrganizationDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ty.cm.constant.Messages.EXISTS_ORG_CODE;
import static com.ty.cm.constant.Messages.RELATED_DATA_DELETE;
import static com.ty.cm.constant.Numbers.ZERO;

/**
 * 组织机构业务逻辑实现
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private SysUserOrganizationService sysUserOrganizationService;

    @Autowired
    private ThirdAppConfigService thirdAppConfigService;

    @Autowired
    private VideoDeviceService videoDeviceService;

    @Autowired
    private Cache cache;

    /**
     * 根据条件获取组织机构的总记录数
     *
     * @param organization 组织机构
     * @return int
     * @throws Exception
     */
    @Override
    public int getCount(Organization organization) throws Exception {
        if (null == organization) {
            organization = new Organization();
        }
        return organizationDao.findOrganizationCount(organization);
    }

    /**
     * 根据条件查询所有组织机构数据
     *
     * @param organization 组织机构
     * @return List<Organization>
     * @throws Exception
     */
    @Override
    public List<Organization> getAll(Organization organization) throws Exception {
        if (null == organization) {
            organization = new Organization();
        }
        return organizationDao.findOrganization(organization);
    }

    /**
     * 保存组织机构数据
     *
     * @param organization 组织机构
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(Organization organization) throws Exception {
        int n = 0;
        if (null != organization) {
            try {
                organization.setOrgId(UUSNUtil.nextUUSN());
                organization.setUpdateUser(organization.getCreateUser());
                n = organizationDao.saveOrganization(organization);
                this.putOrg2Cache(organization);
            } catch (DuplicateKeyException e) {
                throw new CustomException(EXISTS_ORG_CODE);
            }
        }
        return n;
    }

    /**
     * 根据条件查询单条组织机构数据
     *
     * @param organization 组织机构
     * @return Organization
     * @throws Exception
     */
    @Override
    public Organization getOne(Organization organization) throws Exception {
        if (organization != null) {
            List<Organization> organizationList = organizationDao.findOrganization(organization);
            if (!organizationList.isEmpty()) {
                return organizationList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询组织机构数据
     *
     * @param id ID
     * @return Organization
     * @throws Exception
     */
    @Override
    public Organization getById(String id) throws Exception {
        Organization organization = null;
        if (StringUtils.isNotBlank(id)) {
            organization = organizationDao.findOrganizationById(id);
        }
        return organization;
    }

    /**
     * 更新组织机构数据
     *
     * @param organization 组织机构
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(Organization organization) throws Exception {
        int n = 0;
        if (null != organization) {
            try {
                n = organizationDao.updateOrganization(organization);
                this.putOrg2Cache(organization);
            } catch (DuplicateKeyException e) {
                throw new CustomException(EXISTS_ORG_CODE);
            }
        }
        return n;
    }

    /**
     * 删除组织机构数据
     *
     * @param organization 组织机构
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(Organization organization) throws Exception {
        int n = 0;
        if (null != organization && StringUtils.isNotBlank(organization.getOrgId())) {
            try {
                n = organizationDao.delOrganization(organization);
                cache.hdelete(CacheKey.ORGANIZATIONS.value(), organization.getOrgId());
            } catch (DataIntegrityViolationException e) {
                throw new CustomException(RELATED_DATA_DELETE);
            }
        }
        return n;
    }

    /**
     * 根据ID删除组织机构数据
     *
     * @param id ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(String id) throws Exception {
        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            /*
             * 判断待删除的数据，是否存在依赖关系
             */

            // 与用户是否存在关联关系
            if (sysUserOrganizationService.getCount(new SysUserOrganization().setOrgId(id)) > ZERO) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            // 是否为其它机构的上级机构
            if (this.getCount(new Organization().setParentId(id)) > ZERO) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            // 删除机构第三方配置
            thirdAppConfigService.delete(new ThirdAppConfig().setOrgId(id));

            // 删除机构视频设备
            videoDeviceService.delete(new VideoDevice().setOrgId(id));

            // 删除机构
            Organization organization = new Organization();
            organization.setOrgId(id);
            n = this.delete(organization);
        }
        return n;
    }

    /**
     * 获取指定机构的所有子机构
     *
     * @param orgId 机构ID
     * @return List<Organization>
     * @throws Exception
     */
    @Override
    public List<Organization> getOrgChildren(String orgId) throws Exception {
        List<Organization> subOrgList = Lists.newArrayList();
        if (StringUtils.isNotBlank(orgId)) {
            subOrgList = organizationDao.findOrgChildren(orgId);
        }
        return subOrgList;
    }

    /**
     * 获取机构的根机构
     *
     * @param orgId 机构ID
     * @return Organization
     * @throws Exception
     */
    @Override
    public Organization getRootOrganization(String orgId) throws Exception {
        Organization org = null;
        if (StringUtils.isNotBlank(orgId)) {
            String idName = organizationDao.findRootOrganization(orgId);
            if (StringUtils.isNotBlank(idName)) {
                int idx = 20;
                org = new Organization();
                org.setOrgId(idName.substring(0, idx)).setOrgName(idName.substring(20));
            } else {
                log.warn("没有查询到#{}#的根结构", orgId);
            }
        }
        return org;
    }

    /**
     * 添加机构到缓存
     *
     * @param organization 机构对象
     */
    private void putOrg2Cache(Organization organization){
        Organization cacheOrg = new Organization();
        cacheOrg.setOrgId(organization.getOrgId());
        cacheOrg.setCityId(organization.getCityId());
        cacheOrg.setOrgName(organization.getOrgName());
        cacheOrg.setOrgCode(organization.getOrgCode());
        cache.hset(CacheKey.ORGANIZATIONS.value(), organization.getOrgId(), cacheOrg, CacheKey.ORGANIZATIONS.ttl());
    }
}

