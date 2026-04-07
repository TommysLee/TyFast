package com.ty.web.others.controller;

import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.uusn.UUSNUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * General Controller
 *
 * @Author Tommy
 * @Date 2025/8/24
 */
@RestController
@RequestMapping("/general")
public class GeneralController {

    /**
     * 生成UUSN
     */
    @GetMapping("/uusn")
    public AjaxResult uusn() {
        Object u = UUSNUtil.nextUUSN();
        return AjaxResult.success(u);
    }
}
