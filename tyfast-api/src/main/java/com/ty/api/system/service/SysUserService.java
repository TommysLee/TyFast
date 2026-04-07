package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.SysMenu;
import com.ty.api.model.system.SysUser;

/**
 * 用户业务逻辑接口
 *
 * @Author Tommy
 * @Date 2022/1/28
 */
public interface SysUserService extends BaseService<SysUser> {

    /**
     * 仅更新用户数据
     *
     * @param sysUser 用户
     * @return int
     * @throws Exception
     */
    int updateOnly(SysUser sysUser) throws Exception;

    /**
     * 校验原密码是否正确
     *
     * @param sysUser 用户
     * @return boolean
     * @throws Exception
     */
    boolean checkPassword(SysUser sysUser) throws Exception;

    /**
     * 修改密码
     *
     * @param sysUser 用户
     * @return boolean
     * @throws Exception
     */
    boolean updatePassword(SysUser sysUser) throws Exception;

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return boolean
     * @throws Exception
     */
    boolean resetPassword(String userId, String newPassword) throws Exception;

    /**
     * 将此用户的所有会话踢下线
     *
     * @param sysUser 用户
     * @param excludeSid 不包含这些会话
     * @return boolean
     * @throws Exception
     */
    boolean kickOut(SysUser sysUser, String ... excludeSid) throws Exception;

    /**
     * 根据用户ID获取默认首页
     *
     * @param userId 用户ID
     * @return SysMenu
     * @throws Exception
     */
    SysMenu getHomeById(String userId) throws Exception;

    /**
     * 更新用户默认首页
     *
     * @param userId 用户ID
     * @param homeAction 菜单ID
     * @throws Exception
     */
    void updateHome(String userId, String homeAction) throws Exception;

    /**
     * 清除用户默认首页
     *
     * @param userId 用户ID
     * @throws Exception
     */
    void clearHome(String userId) throws Exception;

    /**
     * 根据微信UnionID查询用户数
     *
     * @param unionId
     * @return int
     * @throws Exception
     */
    int getCountByUnionId(String unionId) throws Exception;

    /**
     * 根据微信UnionID获取用户基本信息
     *
     * @param unionId
     * @return SysUser
     * @throws Exception
     */
    SysUser getByUnionId(String unionId) throws Exception;

    /**
     * 根据用户名查询绑定的微信UnionID
     *
     * @param loginName 用户名
     * @return String
     * @throws Exception
     */
    String getUnionId(String loginName) throws Exception;

    /**
     * 保存微信的绑定关系
     *
     * @param unionId   UnionID
     * @param loginName 用户名
     * @return int 返回受影响的行数
     * @throws Exception
     */
    int saveUnionId(String unionId, String loginName) throws Exception;

    /**
     * 清除微信的绑定关系
     *
     * @param loginName 用户名
     * @return int 返回受影响的行数
     * @throws Exception
     */
    int clearUnionId(String loginName) throws Exception;
}
