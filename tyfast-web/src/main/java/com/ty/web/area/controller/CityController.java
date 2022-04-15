package com.ty.web.area.controller;

import com.ty.api.model.area.City;
import com.ty.api.area.service.CityService;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 市Controller
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Controller
@RequestMapping("/area/city")
public class CityController extends BaseController {

    @Autowired
    private CityService cityService;

    /**
     * 跳转到市列表页面
     */
    @GetMapping("/view")
    public String view() {
        return "area/city";
    }

    /**
     * 分页查询市列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public AjaxResult list(City city, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(cityService.query(city, page, pageSize));
    }

    /**
     * 增加市
     */
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(City city) throws Exception {

        int n = cityService.save(city);
        return AjaxResult.success(n);
    }

    /**
     * 查询市明细
     */
    @GetMapping("/single/{cityId}")
    @ResponseBody
    public AjaxResult single(@PathVariable String cityId) throws Exception {
        return AjaxResult.success(cityService.getById(cityId));
    }

    /**
     * 修改市
     */
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(City city) throws Exception {

        int n = cityService.update(city);
        return AjaxResult.success(n);
    }

    /**
     * 删除市
     */
    @GetMapping("/del/{cityId}")
    @ResponseBody
    public AjaxResult del(@PathVariable String cityId) throws Exception {

        int n = cityService.delete(cityId);
        return AjaxResult.success(n);
    }
}
