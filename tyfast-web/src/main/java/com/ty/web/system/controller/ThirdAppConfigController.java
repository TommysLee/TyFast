package com.ty.web.system.controller;

import com.ty.api.model.system.ThirdAppConfig;
import com.ty.api.system.service.ThirdAppConfigService;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方应用参数配置Controller
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@RestController
@RequestMapping("/system/thirdConfig")
public class ThirdAppConfigController extends BaseController {

    @Autowired
    private ThirdAppConfigService thirdAppConfigService;

    /**
     * 查询机构的第三方应用参数配置列表
     */
    @RequestMapping("/list/{orgId}")
    public AjaxResult list(ThirdAppConfig thirdAppConfig) throws Exception {
        return AjaxResult.success(thirdAppConfigService.getAll(thirdAppConfig));
    }

    /**
     * 增加第三方应用参数配置
     */
    @PostMapping("/save")
    public AjaxResult save(ThirdAppConfig thirdAppConfig) throws Exception {
        thirdAppConfig.setCreateUser(getCurrentUserId());
        int n = thirdAppConfigService.save(thirdAppConfig);
        return AjaxResult.success(n);
    }
}