package com.ty.web.shiro.realm;

import com.google.common.collect.Sets;
import com.ty.api.log.service.LoginAuditLogService;
import com.ty.api.model.log.LoginAuditLog;
import com.ty.api.model.system.SysUser;
import com.ty.api.model.system.SysUserRole;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.constant.ShiroConstant;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.StringUtil;
import com.ty.cm.utils.cache.Cache;
import com.ty.cm.utils.crypto.RSA;
import com.ty.web.shiro.AuthenticationToken;
import com.ty.web.utils.WebIpUtil;
import com.ty.web.utils.WebUtil;
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
import static com.ty.cm.constant.Numbers.TWO;
import static com.ty.cm.constant.ShiroConstant.SESSION_TIMEOUT;
import static com.ty.cm.constant.ShiroConstant.STRATEGY_AUTHC;
import static com.ty.cm.constant.ShiroConstant.STRATEGY_URL;
import static com.ty.cm.constant.Ty.SLASH;

/**
 * ????????????Realm
 *
 * ???????????????
 * Shiro??? @Autowired ???????????????????????? ????????? @Lazy?????????
 *
 * ?????????Shiro??????????????????Spring???????????????????????????????????????@Autowire??????Shiro???????????????????????????Spring???????????????
 * ????????? ??????????????????
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Slf4j
public class AuthenticationRealm extends AuthorizingRealm {

    /** ?????????????????? **/
    @Autowired
    @Lazy
    private SysUserService sysUserService;

    /** ???????????????????????? **/
    @Autowired
    @Lazy
    private SysUserRoleService sysUserRoleService;

    /** ?????????????????? **/
    @Autowired
    @Lazy
    private LoginAuditLogService loginAuditLogService;

    /** ???????????? **/
    @Autowired
    @Lazy
    private Cache cache;

    /** ??????URL???????????? **/
    /** ?????????/{TenantID}/{URL}/{Param} **/
    /** {TenantID} ??? {Param} ????????????????????? **/
    final Pattern urlPattern = Pattern.compile("((\\/)*([\\d]+))?([a-z\\/\\_\\-]+)((\\/)[\\d\\/]+)?", Pattern.CASE_INSENSITIVE);

    /**
     * ????????????
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) throws AuthenticationException {

        final AuthenticationToken authenticationToken = (AuthenticationToken) token;
        String username = authenticationToken.getUsername();
        String password = null != authenticationToken.getPassword()? new String(authenticationToken.getPassword()) : null;

        // ????????????????????????????????????
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {

            // ????????????
            SysUser sysUser = new SysUser();
            sysUser.setLoginName(username);
            sysUser.setIsLike(false);
            try {
                sysUser = sysUserService.getOne(sysUser);
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                throw authenticationToken.setAex(new UnknownAccountException(ERROR_MSG_EXCEPTION));
            }

            // ???????????????
            if (null == sysUser) {
                throw authenticationToken.setAex(new UnknownAccountException(ERROR_MSG_ACCOUNT_NON_EXIST));
            }

            // ???????????????
            if (ShiroConstant.ACCOUNT_FROZEN.equals(sysUser.getStatus())) {
                throw authenticationToken.setAex(new LockedAccountException(ERROR_MSG_ACCOUNT_LOCKED));
            }

            // ????????????
            if (!DataUtil.encrypt(RSA.decrypt(password), sysUser.getSalt()).equals(sysUser.getPassword())) {
                throw authenticationToken.setAex(new IncorrectCredentialsException(ERROR_MSG_ACCOUNT));
            }

            // ????????????????????????clean????????????????????????????????????
            return new SimpleAuthenticationInfo(sysUser.clean(), password, getName());
        }
        throw authenticationToken.setAex(new UnknownAccountException(ERROR_MSG_ACCOUNT_UNKNOWN));
    }

    /**
     * ??????????????????
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        final SysUser account = (SysUser) principals.getPrimaryPrincipal(); // ???Realm??????????????????????????????
        if (null != account) {
            // ???Redis????????? "????????????"
            Set<String> roles = cache.getAndTouch(account.getRoleKey(), SESSION_TIMEOUT);
            if (null == roles) {
                roles = Sets.newHashSet();
                // ????????????????????? "????????????"
                if (StringUtils.isNotBlank(account.getUserId())) {
                    try {
                        List<SysUserRole> userRoleList = sysUserRoleService.getAll(new SysUserRole().setUserId(account.getUserId()));
                        for (SysUserRole item : userRoleList) {
                            if (StringUtils.isNotBlank(item.getRoleId())) {
                                roles.add(item.getRoleId());
                            }
                        }
                    } catch(Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }

                // ?????? "????????????" ???Redis
                cache.set(account.getRoleKey(), roles, SESSION_TIMEOUT);
            }

            // ???Redis????????? "??????" ?????????????????????Shiro????????????
            try {
                Set<String> urls = sysUserRoleService.getUserPermission(roles);
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
     * ????????????
     */
    @Override
    protected boolean isPermitted(Permission permission, AuthorizationInfo info) {

        boolean hasPermit = super.isPermitted(permission, info);
        String permissionText = permission.toString();

        // ???????????????????????????????????????????????????
        if (!hasPermit) {
           // ?????? authc ??????
            if (STRATEGY_AUTHC.equals(permissionText)) {
                hasPermit = true;
            } else if (STRATEGY_URL.equals(permissionText)) { // ????????????URL???????????????
                SimpleAuthorizationInfo authorizationInfo = (SimpleAuthorizationInfo) info;
                if (null != authorizationInfo) {
                    // ?????????????????????????????????
                    Set<String> permitSet = authorizationInfo.getStringPermissions();
                    if (null != permitSet) {
                        // ????????????URL
                        String currentUrl = WebUtils.getPathWithinApplication(WebUtils.getHttpRequest(SecurityUtils.getSubject()));

                        // ????????????
                        hasPermit = permitSet.contains(currentUrl);
                        if (!hasPermit) { // ???????????????URL?????????????????????
                            Matcher matcher = urlPattern.matcher(currentUrl);
                            if (matcher.matches()) {
                                log.debug("??????TenantID???" + matcher.group(3));
                                log.debug("URL???" +  matcher.group(4));
                                log.debug("?????????" + matcher.group(5));

                                currentUrl = StringUtil.trim(matcher.group(4), SLASH, SLASH);
                                log.debug("????????????URL???" + currentUrl);

                                hasPermit = permitSet.contains(currentUrl);
                            }
                        }
                    }
                }
            }
        }
        return hasPermit;
    }

    /**
     * ??????????????????
     */
    @Override
    public void onLogout(PrincipalCollection principals) {
        if (null != principals && !principals.isEmpty()) {
            try {
                final SysUser account = (SysUser) principals.getPrimaryPrincipal();
                // ????????????(????????????)??????
                loginAuditLogService.save(new LoginAuditLog(account.getLoginName(), WebIpUtil.getClientIP(), WebUtil.getUserAgent(), TWO));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        // ??????????????????
        super.onLogout(principals);
    }
}
