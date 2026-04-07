package com.ty.web.system.controller;

import com.google.common.collect.Sets;
import com.ty.api.model.system.SysUserOrganization;
import com.ty.api.system.service.SysUserOrganizationService;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.ty.cm.constant.Numbers.ZERO;

/**
 * 用户和机构关联表Controller
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@RestController
@RequestMapping("/system/uorg")
public class SysUserOrganizationController extends BaseController {

    @Autowired
    private SysUserOrganizationService sysUserOrganizationService;

    /**
     * 查询用户和机构关联表列表
     */
    @RequestMapping("/list/{userId}")
    public AjaxResult list(@PathVariable String userId) throws Exception {
        return AjaxResult.success(sysUserOrganizationService.getAll(new SysUserOrganization().setUserId(userId).setIsDefault(ZERO)));
    }

    /**
     * 增加用户和机构关联表
     */
    @PostMapping("/save/{userId}")
    public AjaxResult save(@PathVariable String userId, String[] ids) throws Exception {
        ids = Optional.ofNullable(ids).orElse(new String[0]);
        SysUserOrganization uorg = new SysUserOrganization().setUserId(userId);
        uorg.setIds(Sets.newHashSet(ids));
        return AjaxResult.success(sysUserOrganizationService.saveRelated(uorg));
    }
}
