package com.ty.web.shiro.realm;

import com.ty.api.system.service.SysUserService;
import com.ty.cm.utils.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 安全数据Realm
 *
 * 重要提示：
 * Shiro中 @Autowired 注入的类，一定要 再添加 @Lazy注解！
 *
 * 因为：Shiro框架初始化比Spring框架的某些部件早，导致使用@Autowire注入Shiro框架的某些类不能被Spring正确初始化
 * 会导致 事务失效等。
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Slf4j
public abstract class AuthenticationRealm extends AuthorizingRealm {

    /** 账户业务接口 **/
    @Autowired
    @Lazy
    protected SysUserService sysUserService;

    /** 缓存对象 **/
    @Autowired
    @Lazy
    protected Cache cache;

    /**
     * 获取权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }
}
