package com.ty.web.system.controller;

import com.ty.api.system.service.SysUserService;
import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.cache.Cache;
import com.ty.web.base.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方平台账号绑定Controller
 *
 * @Author Tommy
 * @Date 2025/6/21
 */
@RestController
@RequestMapping("/user/bind")
@Slf4j
public class BindController extends BaseController {

    @Autowired
    protected SysUserService sysUserService;

    @Autowired
    private Cache cache;

    /**
     * 微信绑定状态
     */
    @GetMapping("/state/weixin")
    public AjaxResult stateWx() throws Exception {
        return AjaxResult.success(StringUtils.isNotBlank(sysUserService.getUnionId(getCurrentLoginName())) ? 1 : 0);
    }

    /**
     * 保存微信绑定关系
     */
    @PostMapping("/save/weixin")
    public AjaxResult saveBindWx(String unionId) throws Exception {
        return AjaxResult.success(sysUserService.saveUnionId(unionId, getCurrentLoginName()));
    }

    /**
     * 清除微信绑定关系
     */
    @PostMapping("/clear/weixin")
    public AjaxResult clearBindWx() throws Exception {
        return AjaxResult.success(sysUserService.clearUnionId(getCurrentLoginName()));
    }
}
