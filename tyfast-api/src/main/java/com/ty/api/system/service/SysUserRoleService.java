package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.SysUserRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户和角色关联表业务逻辑接口
 *
 * @Author TyCode
 * @Date 2022/01/29
 */
public interface SysUserRoleService extends BaseService<SysUserRole> {

    /**
     * 查询不符合条件的所有用户角色列表
     *
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     * @throws Exception
     */
    public List<SysUserRole> getAllNot(SysUserRole sysUserRole) throws Exception;

    /**
     * 查询授予用户的菜单和权限
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     * @throws Exception
     */
    public List<SysUserRole> getSysUserRoleGrant(SysUserRole sysUserRole) throws Exception;

    /**
     * 获取用户的可以访问的所有菜单和权限
     *
     * @param sysUserRole 用户和角色关联表
     * @return Map<String, Set<String>>
     * @throws Exception
     */
    public Map<String, Set<String>> getMenuAndPermission(SysUserRole sysUserRole) throws Exception;
}
