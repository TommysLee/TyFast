package com.ty.logic.system.dao;

import com.ty.api.model.system.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色和菜单关联表数据访问层
 *
 * @Author TyCode
 * @Date 2022/02/07
 */
@Mapper
public interface SysRoleMenuDao {

    /**
     * 根据条件查询角色和菜单关联表记录数
     * @param sysRoleMenu 角色和菜单关联表
     * @return int
     */
    public int findSysRoleMenuCount(SysRoleMenu sysRoleMenu);

    /**
     * 根据条件查询所有角色和菜单关联表数据
     * @param sysRoleMenu 角色和菜单关联表
     * @return List<SysRoleMenu>
     */
    public List<SysRoleMenu> findSysRoleMenu(SysRoleMenu sysRoleMenu);

    /**
     * 批量保存角色和菜单关联表数据
     *
     * @param list 角色和菜单关联表集合
     * @return int 返回受影响的行数
     */
    public int saveMultiSysRoleMenu(List<SysRoleMenu> list);

    /**
     * 删除角色和菜单关联表数据
     *
     * @param sysRoleMenu 角色和菜单关联表
     * @return int 返回受影响的行数
     */
    public int delSysRoleMenu(SysRoleMenu sysRoleMenu);
}
