package com.ty.logic.system.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.system.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 用户数据访问层
 *
 * @Author Tommy
 * @Date 2022/1/28
 */
@Mapper
public interface SysUserDao {

    /**
     * 根据条件查询用户记录数
     * @param sysUser 用户
     * @return int
     */
    public int findSysUserCount(SysUser sysUser);

    /**
     * 根据条件查询所有用户数据
     * @param sysUser 用户
     * @return List<SysUser>
     */
    public List<SysUser> findSysUser(SysUser sysUser);

    /**
     * 根据条件分页查询用户数据
     * @param rowBounds 分页参数
     * @param sysUser 用户
     * @return Page<SysUser>
     */
    public Page<SysUser> findSysUser(RowBounds rowBounds, SysUser sysUser);

    /**
     * 根据ID查询用户数据
     *
     * @param userId 用户ID
     * @return SysUser
     */
    public SysUser findSysUserById(String userId);

    /**
     * 保存用户数据
     *
     * @param sysUser 用户
     * @return int 返回受影响的行数
     */
    public int saveSysUser(SysUser sysUser);

    /**
     * 批量保存用户数据
     *
     * @param list 用户集合
     * @return int 返回受影响的行数
     */
    public int saveMultiSysUser(List<SysUser> list);

    /**
     * 更新用户数据
     *
     * @param sysUser 用户
     * @return int 返回受影响的行数
     */
    public int updateSysUser(SysUser sysUser);

    /**
     * 修改密码
     *
     * @param sysUser 用户
     * @return 返回受影响的行数
     */
    public int updatePassword(SysUser sysUser);

    /**
     * 删除用户数据
     *
     * @param sysUser 用户
     * @return int 返回受影响的行数
     */
    public int delSysUser(SysUser sysUser);

    /**
     * 批量删除用户数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     */
    public int delMultiSysUser(List<String> ids);
}
