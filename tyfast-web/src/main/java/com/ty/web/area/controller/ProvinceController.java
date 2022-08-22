package com.ty.web.area.controller;

import com.ty.api.area.service.ProvinceService;
import com.ty.api.model.area.Province;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 省Controller
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@RestController
@RequestMapping("/area/province")
public class ProvinceController extends BaseController {

    @Autowired
    private ProvinceService provinceService;

    /**
     * 跳转到省列表页面
     */
    @GetMapping("/view")
    public ModelAndView view() {
        return new ModelAndView("area/province");
    }

    /**
     * 查询省列表
     */
    @RequestMapping("/list")
    public AjaxResult list(Province province) throws Exception {
        return AjaxResult.success(provinceService.getAll(province));
    }

    /**
     * 增加省
     */
    @PostMapping("/save")
    public AjaxResult save(Province province) throws Exception {

        int n = provinceService.save(province);
        return AjaxResult.success(n);
    }

    /**
     * 查询省明细
     */
    @GetMapping("/single/{provinceId}")
    public AjaxResult single(@PathVariable String provinceId) throws Exception {
        return AjaxResult.success(provinceService.getById(provinceId));
    }

    /**
     * 修改省
     */
    @PostMapping("/update")
    public AjaxResult update(Province province) throws Exception {

        int n = provinceService.update(province);
        return AjaxResult.success(n);
    }

    /**
     * 删除省
     */
    @GetMapping("/del/{provinceId}")
    public AjaxResult del(@PathVariable String provinceId) throws Exception {

        int n = provinceService.delete(provinceId);
        return AjaxResult.success(n);
    }
}
