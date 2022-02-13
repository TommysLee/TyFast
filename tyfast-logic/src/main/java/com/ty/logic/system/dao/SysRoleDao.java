package com.ty.logic.system.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.system.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 角色数据访问层
 *
 * @Author TyCode
 * @Date 2022/02/03
 */
@Mapper
public interface SysRoleDao {

    /**
     * 根据条件查询角色记录数
     * @param sysRole 角色
     * @return int
     */
    public int findSysRoleCount(SysRole sysRole);

    /**
     * 根据条件查询所有角色数据
     * @param sysRole 角色
     * @return List<SysRole>
     */
    public List<SysRole> findSysRole(SysRole sysRole);

    /**
     * 根据条件分页查询角色数据
     * @param rowBounds 分页参数
     * @param sysRole 角色
     * @return Page<SysRole>
     */
    public Page<SysRole> findSysRole(RowBounds rowBounds, SysRole sysRole);

    /**
     * 根据ID查询角色数据
     *
     * @param roleId 角色ID
     * @return SysRole
     */
    public SysRole findSysRoleById(String roleId);

    /**
     * 保存角色数据
     *
     * @param sysRole 角色
     * @return int 返回受影响的行数
     */
    public int saveSysRole(SysRole sysRole);

    /**
     * 批量保存角色数据
     *
     * @param list 角色集合
     * @return int 返回受影响的行数
     */
    public int saveMultiSysRole(List<SysRole> list);

    /**
     * 更新角色数据
     *
     * @param sysRole 角色
     * @return int 返回受影响的行数
     */
    public int updateSysRole(SysRole sysRole);

    /**
     * 删除角色数据
     *
     * @param sysRole 角色
     * @return int 返回受影响的行数
     */
    public int delSysRole(SysRole sysRole);

    /**
     * 批量删除角色数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     */
    public int delMultiSysRole(List<String> ids);
}
