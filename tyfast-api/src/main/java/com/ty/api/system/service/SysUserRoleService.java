package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.SysUser;
import com.ty.api.model.system.SysUserRole;

import java.util.List;
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
    List<SysUserRole> getAllNot(SysUserRole sysUserRole) throws Exception;

    /**
     * 查询授予用户的菜单和权限
     *
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     * @throws Exception
     */
    List<SysUserRole> getSysUserRoleGrant(SysUserRole sysUserRole) throws Exception;

    /**
     * 查询授予用户的菜单ID
     *
     * @param roleIds   授予用户的角色ID集合
     * @return List<String>
     * @throws Exception
     */
    Set<String> getUserMenusId(Set<String> roleIds) throws Exception;

    /**
     * 查询授予用户的权限URL
     *
     * @param roleIds   授予用户的角色ID集合
     * @return Set<String>
     * @throws Exception
     */
    Set<String> getUserPermission(Set<String> roleIds) throws Exception;

    /**
     * 查询可授予的角色列表
     *
     * @param userId         要被授权的用户ID
     * @param currentAccount 当前Request的用户
     * @return List<SysUserRole>
     * @throws Exception
     */
    List<SysUserRole> getGrantableRoles(String userId, SysUser currentAccount) throws Exception;
}
