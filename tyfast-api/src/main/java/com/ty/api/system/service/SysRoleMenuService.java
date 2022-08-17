package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.SysRoleMenu;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色和菜单关联表业务逻辑接口
 *
 * @Author TyCode
 * @Date 2022/02/07
 */
public interface SysRoleMenuService extends BaseService<SysRoleMenu> {

    /**
     * 获取角色可以访问的所有菜单和权限
     *
     * @param roleId 角色ID
     * @return Map<String, Set<String>>
     * @throws Exception
     */
    Map<String, Set<String>> getMenuAndPermission(String roleId) throws Exception;

    /**
     * 获取角色可以访问的所有菜单和权限
     *
     * @param roleIdList 角色ID列表
     * @return Map<String, Map<String, Set<String>>>
     * @throws Exception
     */
    Map<String, Map<String, Set<String>>> getMenuAndPermission(List<String> roleIdList) throws Exception;
}
