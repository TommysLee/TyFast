package com.ty.web.area.controller;

import com.ty.api.area.service.DistrictService;
import com.ty.api.model.area.District;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 区县Controller
 *
 * @Author TyCode
 * @Date 2022/04/15
 */
@Controller
@RequestMapping("/area/district")
public class DistrictController extends BaseController {

    @Autowired
    private DistrictService districtService;

    /**
     * 跳转到区县列表页面
     */
    @GetMapping("/view")
    public String view() {
        return "area/district";
    }

    /**
     * 查询区县列表
     */
    @RequestMapping("/list/{cityId}")
    @ResponseBody
    public AjaxResult list(District district) throws Exception {
        return AjaxResult.success(districtService.getAll(district));
    }

    /**
     * 增加区县
     */
    @PostMapping("/save")
    @ResponseBody
    public AjaxResult save(District district) throws Exception {

        int n = districtService.save(district);
        return AjaxResult.success(n);
    }

    /**
     * 查询区县明细
     */
    @GetMapping("/single/{districtId}")
    @ResponseBody
    public AjaxResult single(@PathVariable String districtId) throws Exception {
        return AjaxResult.success(districtService.getById(districtId));
    }

    /**
     * 修改区县
     */
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(District district) throws Exception {

        int n = districtService.update(district);
        return AjaxResult.success(n);
    }

    /**
     * 删除区县
     */
    @GetMapping("/del/{districtId}")
    @ResponseBody
    public AjaxResult del(@PathVariable String districtId) throws Exception {

        int n = districtService.delete(districtId);
        return AjaxResult.success(n);
    }
}
