package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.SysUser;

/**
 * 用户业务逻辑接口
 *
 * @Author Tommy
 * @Date 2022/1/28
 */
public interface SysUserService extends BaseService<SysUser> {

    /**
     * 校验原密码是否正确
     *
     * @param sysUser 用户
     * @return boolean
     * @throws Exception
     */
    public boolean checkPassword(SysUser sysUser) throws Exception;

    /**
     * 修改密码
     *
     * @param sysUser 用户
     * @return boolean
     * @throws Exception
     */
    public boolean updatePassword(SysUser sysUser) throws Exception;

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return boolean
     * @throws Exception
     */
    public boolean resetPassword(String userId, String newPassword) throws Exception;

    /**
     * 将此用户的所有会话踢下线
     *
     * @param sysUser 用户
     * @param excludeSid 不包含这些会话
     * @return boolean
     * @throws Exception
     */
    public boolean kickOut(SysUser sysUser, String ... excludeSid) throws Exception;
}
