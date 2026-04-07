package com.ty.web.shiro.realm;

import com.ty.api.model.system.DictionaryItem;
import com.ty.api.model.system.SysUser;
import com.ty.cm.constant.ShiroConstant;
import com.ty.cm.constant.enums.CacheKey;
import com.ty.web.shiro.AuthenticationToken;
import com.ty.web.spring.config.properties.TyProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

import static com.ty.cm.constant.Messages.ERROR_MSG_ACCOUNT_LOCKED;
import static com.ty.cm.constant.Messages.ERROR_MSG_ACCOUNT_NON_EXIST;
import static com.ty.cm.constant.Messages.ERROR_MSG_EXCEPTION;

/**
 * 免密认证Realm
 *
 * @Author Tommy
 * @Date 2023/1/31
 */
@Slf4j
public class WithoutPasswordRealm extends AuthenticationRealm {

    @Lazy
    @Autowired
    private TyProperties tyProperties;

    /**
     * 认证校验
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken token) throws AuthenticationException {
        final AuthenticationToken authenticationToken = (AuthenticationToken) token;
        String symbol = authenticationToken.getUsername();

        if (StringUtils.isNotBlank(symbol) && null == authenticationToken.getPassword()) {
            String userName = getUserName(symbol);
            log.info("免密登录Realm：" + symbol + " By " + userName);

            if (StringUtils.isNotBlank(userName)) {
                // 查询用户
                SysUser sysUser = new SysUser();
                sysUser.setLoginName(userName);
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

                // 设置用户的租户ID
                if (null != sysUser.getOrg()) {
                    sysUser.setTenantId(sysUser.getOrg().getOrgId());
                }

                // 验证通过后，调用clean方法，置空密码，防止泄露
                String password = sysUser.getPassword();
                authenticationToken.setPassword(password.toCharArray());
                return new SimpleAuthenticationInfo(sysUser.clean(), password, getName());
            }
        }
        return null;
    }

    /*
     * 获取Symbol对应的账户名
     */
    String getUserName(String symbol) {
        String userName = null;
        List<DictionaryItem> itemList = cache.hget(CacheKey.DICT_LIST.value(), tyProperties.getAutoLoginKey());
        if (CollectionUtils.isNotEmpty(itemList)) {
            userName = (String) itemList.stream().filter(item -> symbol.equals(item.getTitle())).map(item -> item.getValue()).findFirst().orElse(null);
        }
        return userName;
    }
}
