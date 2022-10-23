package com.ty.web.log.controller;

import com.ty.api.log.service.LoginAuditLogService;
import com.ty.api.model.log.LoginAuditLog;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录审计日志Controller
 *
 * @Author TyCode
 * @Date 2022/10/23
 */
@RestController
@RequestMapping("/log/login")
public class LoginAuditLogController extends BaseController {

    @Autowired
    private LoginAuditLogService loginAuditLogService;

    /**
     * 分页查询登录审计日志列表
     */
    @RequestMapping("/list")
    public AjaxResult list(LoginAuditLog loginAuditLog, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(loginAuditLogService.query(loginAuditLog, page, pageSize));
    }

    /**
     * 删除登录审计日志
     */
    @GetMapping("/del/{logId}")
    public AjaxResult del(@PathVariable String logId) throws Exception {

        int n = loginAuditLogService.delete(logId);
        return AjaxResult.success(n);
    }
}
