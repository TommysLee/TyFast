package com.ty.logic.system.dao;

import com.ty.api.model.system.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户和角色关联表数据访问层
 *
 * @Author TyCode
 * @Date 2022/02/08
 */
@Mapper
public interface SysUserRoleDao {

    /**
     * 根据条件查询用户和角色关联表记录数
     * @param sysUserRole 用户和角色关联表
     * @return int
     */
    public int findSysUserRoleCount(SysUserRole sysUserRole);

    /**
     * 根据条件查询所有用户角色列表
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     */
    public List<SysUserRole> findSysUserRole(SysUserRole sysUserRole);

    /**
     * 查询不符合条件的所有用户角色列表
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     */
    public List<SysUserRole> findSysUserRoleNot(SysUserRole sysUserRole);

    /**
     * 查询授予用户的菜单和权限
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     */
    public List<SysUserRole> findSysUserRoleGrant(SysUserRole sysUserRole);

    /**
     * 保存用户和角色关联表数据
     *
     * @param sysUserRole 用户和角色关联表
     * @return int 返回受影响的行数
     */
    public int saveSysUserRole(SysUserRole sysUserRole);

    /**
     * 批量保存用户和角色关联表数据
     *
     * @param list 用户和角色关联表集合
     * @return int 返回受影响的行数
     */
    public int saveMultiSysUserRole(List<SysUserRole> list);

    /**
     * 删除用户和角色关联表数据
     *
     * @param sysUserRole 用户和角色关联表
     * @return int 返回受影响的行数
     */
    public int delSysUserRole(SysUserRole sysUserRole);

    /**
     * 批量删除用户和角色关联表数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     */
    public int delMultiSysUserRole(List<String> ids);
}
