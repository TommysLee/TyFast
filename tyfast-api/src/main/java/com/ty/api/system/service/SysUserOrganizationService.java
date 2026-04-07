package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.SysUserOrganization;

/**
 * 用户和机构关联表业务逻辑接口
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
public interface SysUserOrganizationService extends BaseService<SysUserOrganization> {

    /**
     * 保存用户的关联机构
     *
     * @param userOrganization 用户和机构关联表实体对象
     * @return int 返回受影响的行数
     * @throws Exception
     */
    int saveRelated(SysUserOrganization userOrganization) throws Exception;
}
