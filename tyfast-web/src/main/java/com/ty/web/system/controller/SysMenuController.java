package com.ty.web.system.controller;

import com.google.common.collect.Lists;
import com.ty.api.model.system.SysMenu;
import com.ty.api.model.system.SysUser;
import com.ty.api.system.service.SysMenuService;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.cm.constant.enums.MenuType;
import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.cache.Cache;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限Controller
 *
 * @Author TyCode
 * @Date 2022/02/04
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private Cache cache;

    /**
     * 跳转到菜单列表页面
     */
    @GetMapping("/view")
    public ModelAndView view() {
        return new ModelAndView("system/menu/menu");
    }

    /**
     * 查询全部菜单列表
     */
    @RequestMapping("/list")
    public AjaxResult list(SysMenu sysMenu) throws Exception {
        return AjaxResult.success(sysMenuService.getAll(sysMenu));
    }

    /**
     * 获取当前登录用户的菜单
     */
    @RequestMapping("/user/list")
    public AjaxResult userMenulist() throws Exception {

        List<SysMenu> userMenuList = Lists.newArrayList();
        // 获取当前用户能访问的菜单ID
        SysUser account = getCurrentUser();
        Set<String> roles = cache.get(account.getRoleKey());
        List<String> userMenuIds = sysUserRoleService.getUserMenusId(roles);

        // 查询用户菜单列表
        if (userMenuIds.size() > 0) {
            SysMenu sysMenu = new SysMenu();
            sysMenu.setIds(userMenuIds);
            userMenuList = sysMenuService.getAll(sysMenu);
        }
        return AjaxResult.success(userMenuList);
    }

    /**
     * 增加菜单
     */
    @PostMapping("/save")
    public AjaxResult save(SysMenu sysMenu) throws Exception {

        sysMenu.setMenuType(MenuType.M.name());
        sysMenu.setCreateUser(getCurrentUserId());
        int n = sysMenuService.save(sysMenu);
        return AjaxResult.success(n);
    }

    /**
     * 修改菜单
     */
    @PostMapping("/update")
    public AjaxResult update(SysMenu sysMenu) throws Exception {

        sysMenu.setMenuType(MenuType.M.name());
        sysMenu.setUpdateUser(getCurrentUserId());
        int n = sysMenuService.update(sysMenu);
        return AjaxResult.success(n);
    }

    /**
     * 查询菜单权限明细
     */
    @GetMapping("/single/{menuId}")
    public AjaxResult single(SysMenu sysMenu) throws Exception {

        sysMenu = sysMenuService.getById(sysMenu.getMenuId());
        return AjaxResult.success(sysMenu);
    }

    /**
     * 删除菜单权限
     */
    @GetMapping("/del/{menuId}")
    public AjaxResult del(SysMenu sysMenu) throws Exception {

        int n = sysMenuService.delete(sysMenu.getMenuId());
        return AjaxResult.success(n);
    }

    /**
     * 增加功能权限
     */
    @PostMapping("/func/save")
    public AjaxResult saveFunc(SysMenu sysMenu) throws Exception {

        sysMenu.setMenuType(MenuType.F.name());
        sysMenu.setCreateUser(getCurrentUserId());
        int n = sysMenuService.save(sysMenu);
        return AjaxResult.success(n);
    }

    /**
     * 修改功能权限
     */
    @PostMapping("/func/update")
    public AjaxResult updateFunc(SysMenu sysMenu) throws Exception {

        sysMenu.setMenuType(MenuType.F.name());
        sysMenu.setUpdateUser(getCurrentUserId());
        int n = sysMenuService.update(sysMenu);
        return AjaxResult.success(n);
    }
}