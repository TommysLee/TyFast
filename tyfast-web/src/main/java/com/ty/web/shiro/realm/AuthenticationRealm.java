package com.ty.web.shiro.realm;

import com.google.common.collect.Sets;
import com.ty.api.model.system.SysUser;
import com.ty.api.model.system.SysUserRole;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.constant.ShiroConstant;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.StringUtil;
import com.ty.cm.utils.crypto.RSA;
import com.ty.web.shiro.AuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ty.cm.constant.Messages.ERROR_MSG_ACCOUNT;
import static com.ty.cm.constant.Messages.ERROR_MSG_ACCOUNT_LOCKED;
import static com.ty.cm.constant.Messages.ERROR_MSG_ACCOUNT_NON_EXIST;
import static com.ty.cm.constant.Messages.ERROR_MSG_ACCOUNT_UNKNOWN;
import static com.ty.cm.constant.Messages.ERROR_MSG_EXCEPTION;
import static com.ty.cm.constant.ShiroConstant.STRATEGY_AUTHC;
import static com.ty.cm.constant.ShiroConstant.STRATEGY_URL;
import static com.ty.cm.constant.Ty.SLASH;

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
public class AuthenticationRealm extends AuthorizingRealm {

    /** 账户业务接口 **/
    @Autowired
    @Lazy
    private SysUserService sysUserService;

    /** 菜单权限业务接口 **/
    @Autowired
    @Lazy
    private SysUserRoleService sysUserRoleService;

    /** 系统URL正则规范 **/
    /** 组成：/{TenantID}/{URL}/{Param} **/
    /** {TenantID} 和 {Param} 均为纯数字形态 **/
    final Pattern urlPattern = Pattern.compile("((\\/)*([\\d]+))?([a-z\\/\\_\\-]+)((\\/)[\\d\\/]+)?", Pattern.CASE_INSENSITIVE);

    /**
     * 认证校验
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) throws AuthenticationException {

        final AuthenticationToken authenticationToken = (AuthenticationToken) token;
        String username = authenticationToken.getUsername();
        String password = new String(authenticationToken.getPassword());

        // 校验用户状态和账户与密码
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {

            // 查询用户
            SysUser sysUser = new SysUser();
            sysUser.setLoginName(username);
            sysUser.setIsLike(false);
            try {
                sysUser = sysUserService.getOne(sysUser);
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                throw authenticationToken.setAex(new UnknownAccountException(ERROR_MSG_EXCEPTION));
            }

            // 用户不存在
            if (null == sysUser) {
                throw authenticationToken.setAex(new UnknownAccountException(ERROR_MSG_ACCOUNT_NON_EXIST));
            }

            // 账户被冻结
            if (ShiroConstant.ACCOUNT_FROZEN.equals(sysUser.getStatus())) {
                throw authenticationToken.setAex(new LockedAccountException(ERROR_MSG_ACCOUNT_LOCKED));
            }

            // 密码错误
            if (!DataUtil.encrypt(RSA.decrypt(password), sysUser.getSalt()).equals(sysUser.getPassword())) {
                throw authenticationToken.setAex(new IncorrectCredentialsException(ERROR_MSG_ACCOUNT));
            }

            // 验证通过后，获取用户被授予的角色列表
            if (StringUtils.isNotBlank(sysUser.getUserId())) {
                Set<String> roles = Sets.newHashSet();
                try {
                    List<SysUserRole> userRoleList = sysUserRoleService.getAll(new SysUserRole().setUserId(sysUser.getUserId()));
                    userRoleList.stream().filter(item -> StringUtils.isNotBlank(item.getRoleId())).forEach(item -> roles.add(item.getRoleId()));
                    sysUser.setRoles(roles);
                } catch(Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            // 调用clean方法，置空密码，防止泄露
            return new SimpleAuthenticationInfo(sysUser.clean(), password, getName());
        }
        throw authenticationToken.setAex(new UnknownAccountException(ERROR_MSG_ACCOUNT_UNKNOWN));
    }

    /**
     * 获取权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        final SysUser account = (SysUser) principals.getPrimaryPrincipal(); // 单Realm时，获取当前登录用户
        if (null != account) {
            // 从Redis中获取 "授权" 信息，并封装为Shiro鉴权对象
            try {
                Set<String> urls = sysUserRoleService.getUserPermission(account.getRoles());
                final SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
                authorizationInfo.addStringPermissions(urls);
                return authorizationInfo;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 权限验证
     */
    @Override
    protected boolean isPermitted(Permission permission, AuthorizationInfo info) {

        boolean hasPermit = super.isPermitted(permission, info);
        String permissionText = permission.toString();

        // 如果校验失败，则执行如下自定义策略
        if (!hasPermit) {
           // 执行 authc 策略
            if (STRATEGY_AUTHC.equals(permissionText)) {
                hasPermit = true;
            } else if (STRATEGY_URL.equals(permissionText)) { // 执行基于URL鉴权的策略
                SimpleAuthorizationInfo authorizationInfo = (SimpleAuthorizationInfo) info;
                if (null != authorizationInfo) {
                    // 获取当前用户的权限列表
                    Set<String> permitSet = authorizationInfo.getStringPermissions();
                    if (null != permitSet) {
                        // 当前请求URL
                        String currentUrl = WebUtils.getPathWithinApplication(WebUtils.getHttpRequest(SecurityUtils.getSubject()));

                        // 权限验证
                        hasPermit = permitSet.contains(currentUrl);
                        if (!hasPermit) { // 按规则提取URL部分，再次验证
                            Matcher matcher = urlPattern.matcher(currentUrl);
                            if (matcher.matches()) {
                                log.debug("租户TenantID：" + matcher.group(3));
                                log.debug("URL：" +  matcher.group(4));
                                log.debug("参数：" + matcher.group(5));

                                currentUrl = StringUtil.trim(matcher.group(4), SLASH, SLASH);
                                log.debug("再次验证URL：" + currentUrl);

                                hasPermit = permitSet.contains(currentUrl);
                            }
                        }
                    }
                }
            }
        }
        return hasPermit;
    }
}
