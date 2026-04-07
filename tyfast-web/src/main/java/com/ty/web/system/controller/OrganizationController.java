package com.ty.web.system.controller;

import com.google.common.collect.Lists;
import com.ty.api.model.system.Organization;
import com.ty.api.model.system.SysUser;
import com.ty.api.system.service.OrganizationService;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 组织机构Controller
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@RestController
@RequestMapping("/system/org")
public class OrganizationController extends BaseController {

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 查询组织机构列表
     */
    @RequestMapping("/list")
    public AjaxResult list() throws Exception {
        return AjaxResult.success(this.queryList(false));
    }

    /**
     * 增加组织机构
     */
    @PostMapping("/save")
    public AjaxResult save(Organization organization) throws Exception {
        organization.setCreateUser(getCurrentUserId());
        int n = organizationService.save(organization);
        return AjaxResult.success(n);
    }

    /**
     * 查询组织机构明细
     */
    @GetMapping("/single/{orgId}")
    public AjaxResult single(@PathVariable String orgId) throws Exception {
        return AjaxResult.success(organizationService.getById(orgId));
    }

    /**
     * 修改组织机构
     */
    @PostMapping("/update")
    public AjaxResult update(Organization organization) throws Exception {
        organization.setUpdateUser(getCurrentUserId());
        int n = organizationService.update(organization);
        return AjaxResult.success(n);
    }

    /**
     * 删除组织机构
     */
    @GetMapping("/del/{orgId}")
    public AjaxResult del(@PathVariable String orgId) throws Exception {
        int n = organizationService.delete(orgId);
        return AjaxResult.success(n);
    }

    /**
     * 查询当前用户可见的组织机构列表
     */
    @RequestMapping("/visible/list")
    public AjaxResult availableList() throws Exception {
        List<Organization> orgList = this.queryList(true);
        for (Organization org : orgList) {
            org.clean();
        }
        return AjaxResult.success(orgList);
    }

    /*
     * 分权限，查询机构列表
     */
    List<Organization> queryList(boolean enableCache) throws Exception {
        // 管理员查询所有机构列表，普通用户查询可见的机构列表
        Organization param = new Organization();
        if (!isSysUser()) {
            if (enableCache) {
                param = this.accessibleOrg();
            } else { // 从DB查询可见机构列表ID
                SysUser user = new SysUser();
                user.setUserId(getCurrentUserId());
                user = sysUserService.getOne(user);
                if (null != user && MapUtils.isNotEmpty(user.getOrgMap())) {
                    param.setIds(user.getOrgMap().keySet());
                } else {
                    param = null;
                }
            }
        }
        List<Organization> orgList = Lists.newArrayList();
        if (null != param) {
            orgList = organizationService.getAll(param);
        }
        return orgList;
    }
}
