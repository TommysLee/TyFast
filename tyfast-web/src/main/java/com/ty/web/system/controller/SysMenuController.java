package com.ty.web.system.controller;

import com.google.common.collect.Lists;
import com.ty.api.model.system.SysMenu;
import com.ty.api.system.service.SysMenuService;
import com.ty.cm.constant.enums.MenuType;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ty.cm.constant.ShiroConstant.CACHE_USER_PERMISSION;
import static com.ty.cm.constant.ShiroConstant.USER_MENU_IDS;

/**
 * 菜单权限Controller
 *
 * @Author TyCode
 * @Date 2022/02/04
 */
@Controller
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 跳转到菜单列表页面
     */
    @GetMapping("/view")
    public String view() {
        return "system/menu/menu";
    }

    /**
     * 查询全部菜单列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public AjaxResult list(SysMenu sysMenu) throws Exception {
        return AjaxResult.success(sysMenuService.getAll(sysMenu));
    }

    /**
     * 获取当前登录用户的菜单
     */
    @RequestMapping("/user/list")
    @ResponseBody
    public AjaxResult userMenulist() throws Exception {

        List<SysMenu> userMenuList = Lists.newArrayList();
        // 获取当前用户能访问的菜单ID
        Map<String, Set<String>> permisMap = getSessionAttribute(CACHE_USER_PERMISSION);
        if (null != permisMap && permisMap.containsKey(USER_MENU_IDS)) {
            // 查询用户菜单列表
            List<String> userMenuIds = Lists.newArrayList(permisMap.get(USER_MENU_IDS));
            if (userMenuIds.size() > 0) {
                SysMenu sysMenu = new SysMenu();
                sysMenu.setIds(userMenuIds);
                userMenuList = sysMenuService.getAll(sysMenu);
            }
        }
        return AjaxResult.success(userMenuList);
    }

    /**
     * 增加菜单
     */
    @PostMapping("/save")
    @ResponseBody
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
    @ResponseBody
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
    @ResponseBody
    public AjaxResult single(SysMenu sysMenu) throws Exception {

        sysMenu = sysMenuService.getById(sysMenu.getMenuId());
        return AjaxResult.success(sysMenu);
    }

    /**
     * 删除菜单权限
     */
    @GetMapping("/del/{menuId}")
    @ResponseBody
    public AjaxResult del(SysMenu sysMenu) throws Exception {

        int n = sysMenuService.delete(sysMenu.getMenuId());
        return AjaxResult.success(n);
    }

    /**
     * 增加功能权限
     */
    @PostMapping("/func/save")
    @ResponseBody
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
    @ResponseBody
    public AjaxResult updateFunc(SysMenu sysMenu) throws Exception {

        sysMenu.setMenuType(MenuType.F.name());
        sysMenu.setUpdateUser(getCurrentUserId());
        int n = sysMenuService.update(sysMenu);
        return AjaxResult.success(n);
    }
}