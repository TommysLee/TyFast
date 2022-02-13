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
}
