package com.ty.logic.system.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.ty.api.model.system.SysUserOrganization;
import com.ty.api.system.service.SysUserOrganizationService;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.SysUserOrganizationDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ty.cm.constant.Numbers.ONE;
import static com.ty.cm.constant.Numbers.ZERO;
import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 用户和机构关联表业务逻辑实现
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Service
@Transactional(readOnly = true)
public class SysUserOrganizationServiceImpl implements SysUserOrganizationService {

    @Autowired
    private SysUserOrganizationDao sysUserOrganizationDao;

    /**
     * 根据条件获取用户和机构关联表的总记录数
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return int
     * @throws Exception
     */
    @Override
    public int getCount(SysUserOrganization sysUserOrganization) throws Exception {
        if (null == sysUserOrganization) {
            sysUserOrganization = new SysUserOrganization();
        }
        return sysUserOrganizationDao.findSysUserOrganizationCount(sysUserOrganization);
    }

    /**
     * 根据条件查询所有用户和机构关联表数据
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return List<SysUserOrganization>
     * @throws Exception
     */
    @Override
    public List<SysUserOrganization> getAll(SysUserOrganization sysUserOrganization) throws Exception {
        if (null == sysUserOrganization) {
            sysUserOrganization = new SysUserOrganization();
        }
        return sysUserOrganizationDao.findSysUserOrganization(sysUserOrganization);
    }

    /**
     * 保存用户和机构关联表数据
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(SysUserOrganization sysUserOrganization) throws Exception {
        int n = 0;
        if (null != sysUserOrganization) {
            n = sysUserOrganizationDao.saveSysUserOrganization(sysUserOrganization);
        }
        return n;
    }

    /**
     * 批量保存用户和机构关联表数据
     *
     * @param list 用户和机构关联表数据列表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int saveBatch(List<SysUserOrganization> list) throws Exception {
        int n = 0;
        if (null != list && list.size() > 0) {
            n = sysUserOrganizationDao.saveMultiSysUserOrganization(list);
        }
        return n;
    }

    /**
     * 保存用户的关联机构
     *
     * @param userOrganization 用户和机构关联表实体对象
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int saveRelated(SysUserOrganization userOrganization) throws Exception {
        int n = 0;
        if (null != userOrganization && StringUtils.isNotBlank(userOrganization.getUserId())) {
            // 删除原关联
            this.delete(new SysUserOrganization().setUserId(userOrganization.getUserId()).setIsDefault(ZERO));

            // 保存
            if (CollectionUtils.isNotEmpty(userOrganization.getIds())) {
                // 获取此用户的默认机构
                Set<String> defaultOrgSet = this.getAll(new SysUserOrganization().setUserId(userOrganization.getUserId()).setIsDefault(ONE)).stream().map(item -> item.getOrgId()).collect(Collectors.toSet());

                // 关联机构集合中要移除默认机构
                List<SysUserOrganization> uorgList = Lists.newArrayList();
                userOrganization.getIds().stream().filter(id -> StringUtils.isNotBlank(id) && !defaultOrgSet.contains(id)).forEach(id -> {
                    uorgList.add(new SysUserOrganization().setUserId(userOrganization.getUserId()).setOrgId(id).setIsDefault(ZERO));
                });

                // 执行
                n = this.saveBatch(uorgList);
            }
        }
        return n;
    }

    /**
     * 删除用户和机构关联表数据
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(SysUserOrganization sysUserOrganization) throws Exception {
        int n = 0;
        if (null != sysUserOrganization && StringUtils.isNotBlank(sysUserOrganization.getUserId())) {
            n = sysUserOrganizationDao.delSysUserOrganization(sysUserOrganization);
        }
        return n;
    }

    /**
     * 根据ID删除用户和机构关联表数据
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
            SysUserOrganization sysUserOrganization = new SysUserOrganization();
            sysUserOrganization.setUserId(id);
            n = this.delete(sysUserOrganization);
        }
        return n;
    }
}