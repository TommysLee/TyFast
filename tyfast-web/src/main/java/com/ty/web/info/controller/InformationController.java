package com.ty.web.info.controller;

import com.ty.api.model.info.Information;
import com.ty.api.info.service.InformationService;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 新闻资讯Controller
 *
 * @Author TyCode
 * @Date 2026/05/23
 */
@RestController
@RequestMapping("/{orgId}/info")
public class InformationController extends BaseController {

    @Autowired
    private InformationService informationService;

    /**
     * 分页查询新闻资讯列表
     */
    @RequestMapping("/list")
    public AjaxResult list(Information information, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(informationService.query(information, page, pageSize));
    }

    /**
     * 增加新闻资讯
     */
    @PostMapping("/save")
    public AjaxResult save(Information information) throws Exception {
        information.setCreateUser(getCurrentUserId());
        int n = informationService.save(information);
        return AjaxResult.success(n);
    }

    /**
     * 查询新闻资讯明细
     */
    @GetMapping("/single/{infoId}")
    public AjaxResult single(@PathVariable String infoId) throws Exception {
        return AjaxResult.success(informationService.getById(infoId));
    }

    /**
     * 修改新闻资讯
     */
    @PostMapping("/update")
    public AjaxResult update(Information information) throws Exception {
        information.setUpdateUser(getCurrentUserId());
        int n = informationService.update(information);
        return AjaxResult.success(n);
    }

    /**
     * 删除新闻资讯
     */
    @GetMapping("/del/{infoId}")
    public AjaxResult del(@PathVariable String infoId) throws Exception {
        int n = informationService.delete(infoId);
        return AjaxResult.success(n);
    }
}
