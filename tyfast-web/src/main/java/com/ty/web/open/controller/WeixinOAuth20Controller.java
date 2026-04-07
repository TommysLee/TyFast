package com.ty.web.open.controller;

import com.ty.api.model.system.SysUser;
import com.ty.cm.constant.enums.CacheKey;
import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.oauth2.core.OAuth20Service;
import com.ty.web.utils.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.ty.cm.constant.NumberString.ONE;
import static com.ty.cm.constant.NumberString.ZERO;
import static com.ty.cm.constant.ShiroConstant.SESSION_TIMEOUT_ANON;

/**
 * 微信 - 第三方登录Controller
 *
 * @Author Tommy
 * @Date 2025/6/21
 */
@Controller
@RequestMapping("/open/oauth2/weixin")
@Slf4j
public class WeixinOAuth20Controller extends OAuth20Controller {

    @Autowired
    @Qualifier("weixinOAuth20Service")
    private OAuth20Service weixinOAuth20Service;

    /**
     * 跳转到微信登录
     */
    @GetMapping("/connect")
    public String connect() throws Exception {
        return "redirect:" + getAuthorizationUrl(ZERO);
    }

    /**
     * 跳转到微信用于绑定
     */
    @GetMapping("/bind/connect")
    @ResponseBody
    public AjaxResult connectForBind() throws Exception {
        return AjaxResult.success(null, getAuthorizationUrl(ONE));
    }

    /**
     * 微信开放平台的回调接口
     */
    @RequestMapping("/callback")
    public String callback(String code, String state, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("微信回调：code={}, state={}", code, state);

        String url = "redirect:/error/timeout.html";
        boolean stateFlag = cache.delete(CacheKey.WX_STATE.value(state));
        if (stateFlag) {
            // 通过code获取access_token
            Map<String, String> tokenMap = weixinOAuth20Service.getAccessToken(code);
            String unionId = tokenMap.get("unionid");

            if (state.endsWith(ONE)) { // 操作类型：账号绑定
                int count = sysUserService.getCountByUnionId(unionId);
                log.info("微信回调: 账户绑定: {}({})", unionId, count);

                request.setAttribute("unionId", unionId);
                request.setAttribute("count", count);
                url = "/system/user/profile/bindwx";
            } else { // 操作类型：联合登录
                // 根据UnionId获取绑定的系统用户
                SysUser user = sysUserService.getByUnionId(unionId);
                if (null != user) {
                    log.info("微信回调: 触发Shiro Login For: {}({})", user.getLoginName(), unionId);
                    url = this.login(user, request, response);
                } else {
                    url = "redirect:/error/unbind.html";
                }
            }
        }
        return url;
    }

    /**
     * 获取微信授权URL
     */
    private String getAuthorizationUrl(String flag) {
        String state = WebUtil.generateSessionId() + flag;
        cache.set(CacheKey.WX_STATE.value(state), null, SESSION_TIMEOUT_ANON);
        log.info("跳转到微信开放平台({})...", state);
        return weixinOAuth20Service.getAuthorizationUrl(state);
    }
}
