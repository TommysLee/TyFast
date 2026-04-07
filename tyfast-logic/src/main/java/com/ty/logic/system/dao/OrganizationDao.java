package com.ty.logic.system.dao;

import com.ty.api.model.system.Organization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 组织机构数据访问层
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Mapper
public interface OrganizationDao {

    /**
     * 根据条件查询组织机构记录数
     *
     * @param organization 组织机构
     * @return int
     */
    int findOrganizationCount(Organization organization);

    /**
     * 根据条件查询所有组织机构数据
     *
     * @param organization 组织机构
     * @return List<Organization>
     */
    List<Organization> findOrganization(Organization organization);

    /**
     * 根据ID查询组织机构数据
     *
     * @param orgId 组织机构ID
     * @return Organization
     */
    Organization findOrganizationById(String orgId);

    /**
     * 查询指定机构的所有子机构
     *
     * @param orgId 机构ID
     * @return List<Organization>
     */
    List<Organization> findOrgChildren(String orgId);

    /**
     * 查询机构的根机构
     *
     * @param orgId 机构ID
     * @return String 格式：ID+Name
     */
    String findRootOrganization(String orgId);

    /**
     * 保存组织机构数据
     *
     * @param organization 组织机构
     * @return int 返回受影响的行数
     */
    int saveOrganization(Organization organization);

    /**
     * 更新组织机构数据
     *
     * @param organization 组织机构
     * @return int 返回受影响的行数
     */
    int updateOrganization(Organization organization);

    /**
     * 删除组织机构数据
     *
     * @param organization 组织机构
     * @return int 返回受影响的行数
     */
    int delOrganization(Organization organization);
}
