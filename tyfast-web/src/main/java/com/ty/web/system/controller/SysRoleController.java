package com.ty.web.system.controller;

import com.google.common.collect.Lists;
import com.ty.api.model.system.SysRole;
import com.ty.api.model.system.SysRoleMenu;
import com.ty.api.system.service.SysRoleMenuService;
import com.ty.api.system.service.SysRoleService;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.DataUtil;
import com.ty.web.base.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Controller
 *
 * @Author Tommy
 * @Date 2022/2/3
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 分页查询角色列表
     */
    @RequestMapping("/list")
    public AjaxResult list(SysRole sysRole, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(sysRoleService.query(sysRole, page, pageSize));
    }

    /**
     * 增加角色
     */
    @PostMapping("/save")
    public AjaxResult save(SysRole sysRole) throws Exception {

        sysRole.setCreateUser(getCurrentUserId());
        int n = sysRoleService.save(sysRole);
        return AjaxResult.success(n);
    }

    /**
     * 查询角色明细
     */
    @GetMapping("/single/{roleId}")
    public AjaxResult single(SysRole sysRole) throws Exception {

        sysRole = sysRoleService.getById(sysRole.getRoleId());
        return AjaxResult.success(sysRole);
    }

    /**
     * 修改角色
     */
    @PostMapping("/update")
    public AjaxResult update(SysRole sysRole) throws Exception {

        sysRole.setUpdateUser(getCurrentUserId());
        int n = sysRoleService.update(sysRole);
        return AjaxResult.success(n);
    }

    /**
     * 删除角色
     */
    @GetMapping("/del/{roleId}")
    public AjaxResult del(SysRole sysRole) throws Exception {

        int n = sysRoleService.delete(sysRole.getRoleId());
        return AjaxResult.success(n);
    }

    /**
     * 批量保存菜单权限授权数据
     */
    @PostMapping("/grant/save")
    public AjaxResult saveGrant(String roleMenuJsonArray, String roleId) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(roleMenuJsonArray) && StringUtils.isNotBlank(roleId)) {
            List<SysRoleMenu> sysRoleMenuList = DataUtil.fromJSONArray(roleMenuJsonArray, SysRoleMenu.class);
            if (sysRoleMenuList.size() > 0) {
                sysRoleMenuList.stream().filter((v) -> {v.setRoleId(roleId); return true;}).collect(Collectors.toList());
                n = sysRoleMenuService.saveBatch(sysRoleMenuList);
            } else {
                sysRoleMenuService.delete(roleId);
            }
        }
        return AjaxResult.success(n);
    }

    /**
     * 根据角色ID查询授权数据
     */
    @GetMapping("/grant/list/{roleId}")
    public AjaxResult grantList(SysRoleMenu sysRoleMenu) throws Exception {

        List<SysRoleMenu> sysRoleMenuList = Lists.newArrayList();
        if (StringUtils.isNotBlank(sysRoleMenu.getRoleId())) {
            sysRoleMenuList = sysRoleMenuService.getAll(sysRoleMenu);
        }
        return AjaxResult.success(sysRoleMenuList);
    }
}
