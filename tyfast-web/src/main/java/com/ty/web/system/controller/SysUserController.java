package com.ty.web.system.controller;

import com.google.common.collect.Lists;
import com.ty.api.model.system.SysUser;
import com.ty.api.model.system.SysUserRole;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import com.ty.web.spring.config.properties.TyProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.ty.cm.constant.Messages.ERROR_PASSWORD;
import static com.ty.cm.constant.Messages.NO_OPERATION;

/**
 * 用户Controller
 *
 * @Author TyCode
 * @Date 2022/02/04
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private TyProperties tyProperties;

    /**
     * 跳转到用户列表页面
     */
    @GetMapping("/view")
    public ModelAndView view() {
        ModelAndView view = new ModelAndView("system/user/user");
        view.addObject("defaultPassword", tyProperties.getInitPassword());
        return view;
    }

    /**
     * 分页查询用户列表
     */
    @RequestMapping("/list")
    public AjaxResult list(SysUser sysUser, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(sysUserService.query(sysUser, page, pageSize));
    }

    /**
     * 增加用户
     */
    @PostMapping("/save")
    public AjaxResult save(SysUser sysUser) throws Exception {

        sysUser.setPassword(tyProperties.getInitPassword()); // 初始密码
        sysUser.setCreateUser(getCurrentUserId());
        int n = sysUserService.save(sysUser);
        return AjaxResult.success(n);
    }

    /**
     * 查询用户明细
     */
    @GetMapping("/single/{userId}")
    public AjaxResult single(SysUser sysUser) throws Exception {

        sysUser = sysUserService.getById(sysUser.getUserId());
        sysUser.setPassword(null);
        sysUser.setSalt(null);
        return AjaxResult.success(sysUser);
    }

    /**
     * 修改用户
     */
    @PostMapping("/update")
    public AjaxResult update(SysUser sysUser) throws Exception {

        sysUser.setUpdateUser(getCurrentUserId());
        int n = sysUserService.update(sysUser);
        return AjaxResult.success(n);
    }

    /**
     * 删除用户
     */
    @GetMapping("/del/{userId}")
    public AjaxResult del(SysUser sysUser) throws Exception {

        int n = sysUserService.delete(sysUser.getUserId());
        return AjaxResult.success(n);
    }

    /**
     * 根据用户ID查询已授予的角色列表
     */
    @GetMapping("/grant/list/{userId}")
    public AjaxResult grantList(SysUserRole sysUserRole) throws Exception {

        List<SysUserRole> sysUserRoleList = Lists.newArrayList();
        if (StringUtils.isNotBlank(sysUserRole.getUserId())) {
            sysUserRoleList = sysUserRoleService.getAll(sysUserRole);
        }
        return AjaxResult.success(sysUserRoleList);
    }

    /**
     * 根据用户ID查询可授予的角色列表
     */
    @GetMapping("/grant/can/list/{userId}")
    public AjaxResult grantCanList(SysUserRole sysUserRole) throws Exception {

        List<SysUserRole> sysUserRoleList = Lists.newArrayList();
        if (StringUtils.isNotBlank(sysUserRole.getUserId())) {
            sysUserRoleList = sysUserRoleService.getAllNot(sysUserRole);
        }
        return AjaxResult.success(sysUserRoleList);
    }

    /**
     * 批量保存角色授权数据
     */
    @PostMapping("/grant/save")
    public AjaxResult saveGrant(SysUserRole sysUserRole) throws Exception {

        String userId = sysUserRole.getUserId();
        if (StringUtils.isNotBlank(userId) && null != sysUserRole.getIds() && sysUserRole.getIds().size() > 0) {
            // 构建用户角色List
            List<SysUserRole> sysUserRoleList = Lists.newArrayList();
            for (String roleId : sysUserRole.getIds()) {
                if (StringUtils.isNotBlank(roleId)) {
                    sysUserRoleList.add(new SysUserRole().setUserId(userId).setRoleId(roleId));
                }
            }

            // 批量保存
            return AjaxResult.success(sysUserRoleService.saveBatch(sysUserRoleList));
        }
        return AjaxResult.warn(NO_OPERATION);
    }

    /**
     * 删除用户角色授权
     */
    @PostMapping("/grant/del/{roleId}")
    public AjaxResult delRole(SysUserRole sysUserRole) throws Exception {

        if (StringUtils.isNotBlank(sysUserRole.getUserId())) {
            return AjaxResult.success(sysUserRoleService.delete(sysUserRole));
        }
        return AjaxResult.warn(NO_OPERATION);
    }

    /**
     * 检验原密码是否正确
     */
    @PostMapping("/secure/check")
    public AjaxResult checkPassword(SysUser sysUser) throws Exception {

        sysUser.setUserId(getCurrentUserId());
        return sysUserService.checkPassword(sysUser)? AjaxResult.success() : AjaxResult.warn(ERROR_PASSWORD);
    }

    /**
     * 修改密码
     */
    @PostMapping("/password/update")
    public AjaxResult updatePassword(SysUser sysUser) throws Exception {

        sysUser.setUserId(getCurrentUserId());
        return sysUserService.updatePassword(sysUser)? AjaxResult.success() : AjaxResult.warn();
    }

    /**
     * 重置密码
     */
    @GetMapping("/password/reset/{userId}")
    public AjaxResult resetPassword(@PathVariable String userId) throws Exception {

        sysUserService.resetPassword(userId, tyProperties.getInitPassword());
        return AjaxResult.success();
    }

    /**
     * 更改登录互踢设置
     */
    @GetMapping("/set/kickout/{userId}/{enableKickOut}")
    public AjaxResult changeKickOut(@PathVariable String userId, @PathVariable Integer enableKickOut) throws Exception {

        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setEnableKickOut(enableKickOut);
        sysUser.setUpdateUser(getCurrentUserId());
        int n = sysUserService.update(sysUser);
        return AjaxResult.success(n);
    }

    /**
     * 获取用户默认首页
     */
    @GetMapping("/profile/myhome")
    public AjaxResult myhome() throws Exception {
        return AjaxResult.success(sysUserService.getHomeById(getCurrentUserId()));
    }

    /**
     * 修改用户默认首页
     */
    @GetMapping("/profile/myhome/update/{homeAction}")
    public AjaxResult updateMyhome(@PathVariable String homeAction) throws Exception {
        sysUserService.updateHome(getCurrentUserId(), homeAction);
        return AjaxResult.success();
    }

    /**
     * 清除用户默认首页
     */
    @GetMapping("/profile/myhome/clear")
    public AjaxResult clearMyhome() throws Exception {
        sysUserService.clearHome(getCurrentUserId());
        return AjaxResult.success();
    }
}