package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.Organization;

import java.util.List;

/**
 * 组织机构业务逻辑接口
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
public interface OrganizationService extends BaseService<Organization> {

    /**
     * 获取指定机构的所有子机构
     *
     * @param orgId 机构ID
     * @return List<Organization>
     * @throws Exception
     */
    List<Organization> getOrgChildren(String orgId) throws Exception;

    /**
     * 获取机构的根机构
     *
     * @param orgId 机构ID
     * @return Organization
     * @throws Exception
     */
    Organization getRootOrganization(String orgId) throws Exception;
}