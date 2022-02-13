package com.ty.logic.system.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 菜单权限数据访问层
 *
 * @Author TyCode
 * @Date 2022/01/29
 */
@Mapper
public interface SysMenuDao {

    /**
     * 根据条件查询菜单权限记录数
     * @param sysMenu 菜单权限
     * @return int
     */
    public int findSysMenuCount(SysMenu sysMenu);

    /**
     * 根据条件查询所有菜单权限数据
     * @param sysMenu 菜单权限
     * @return List<SysMenu>
     */
    public List<SysMenu> findSysMenu(SysMenu sysMenu);

    /**
     * 根据条件分页查询菜单权限数据
     * @param rowBounds 分页参数
     * @param sysMenu 菜单权限
     * @return Page<SysMenu>
     */
    public Page<SysMenu> findSysMenu(RowBounds rowBounds, SysMenu sysMenu);

    /**
     * 根据ID查询菜单权限数据
     *
     * @param menuId 菜单权限ID
     * @return SysMenu
     */
    public SysMenu findSysMenuById(String menuId);

    /**
     * 保存菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return int 返回受影响的行数
     */
    public int saveSysMenu(SysMenu sysMenu);

    /**
     * 批量保存菜单权限数据
     *
     * @param list 菜单权限集合
     * @return int 返回受影响的行数
     */
    public int saveMultiSysMenu(List<SysMenu> list);

    /**
     * 更新菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return int 返回受影响的行数
     */
    public int updateSysMenu(SysMenu sysMenu);

    /**
     * 删除菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return int 返回受影响的行数
     */
    public int delSysMenu(SysMenu sysMenu);

    /**
     * 批量删除菜单权限数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     */
    public int delMultiSysMenu(List<String> ids);
}
