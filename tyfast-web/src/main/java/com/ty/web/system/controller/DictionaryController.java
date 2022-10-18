package com.ty.web.system.controller;

import com.google.common.collect.Lists;
import com.ty.api.model.system.Dictionary;
import com.ty.api.model.system.DictionaryItem;
import com.ty.api.system.service.DictionaryService;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.web.base.controller.BaseController;
import com.ty.web.spring.config.properties.TyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据字典Controller
 *
 * @Author TyCode
 * @Date 2022/10/14
 */
@RestController
@RequestMapping("/system/dict")
public class DictionaryController extends BaseController {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private TyProperties tyProperties;

    /**
     * 分页查询数据字典列表
     */
    @RequestMapping("/list")
    public AjaxResult list(Dictionary dictionary, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(dictionaryService.query(dictionary, page, pageSize));
    }

    /**
     * 增加数据字典
     */
    @PostMapping("/save")
    public AjaxResult save(Dictionary dictionary) throws Exception {

        dictionary.setCreateUser(getCurrentUserId());
        int n = dictionaryService.save(dictionary);
        return AjaxResult.success(n);
    }

    /**
     * 查询数据字典明细
     */
    @GetMapping("/single/{code}")
    public AjaxResult single(@PathVariable String code) throws Exception {
        return AjaxResult.success(dictionaryService.getById(code));
    }

    /**
     * 修改数据字典
     */
    @PostMapping("/update")
    public AjaxResult update(Dictionary dictionary) throws Exception {

        dictionary.setUpdateUser(getCurrentUserId());
        int n = dictionaryService.update(dictionary);
        return AjaxResult.success(n);
    }

    /**
     * 删除数据字典
     */
    @GetMapping("/del")
    public AjaxResult del(@RequestParam String code) throws Exception {

        int n = dictionaryService.delete(code);
        return AjaxResult.success(n);
    }

    /**
     * 重载数据字典到缓存
     */
    @PostMapping("/reload")
    public AjaxResult reload() throws Exception {
        return AjaxResult.success(dictionaryService.loadCache());
    }

    /**
     * 新增或修改字典项
     */
    @PostMapping("/item/merge")
    public AjaxResult update(String code, String items) throws Exception {

        Dictionary dictionary = new Dictionary();
        dictionary.setCode(code);
        dictionary.setOldCode(code);
        dictionary.setItems(items);
        dictionary.setUpdateUser(getCurrentUserId());
        int n = dictionaryService.update(dictionary);
        return AjaxResult.success(n);
    }

    /**
     * 根据Code获取字典值
     */
    @RequestMapping("/items")
    public AjaxResult items(String[] codes) throws Exception {
        return AjaxResult.success(dictionaryService.getItemsByCodes(codes));
    }

    /**
     * 获取语言列表
     */
    @GetMapping("/lang")
    public AjaxResult langlist() throws Exception {
        List<DictionaryItem> items = null;
        String code = tyProperties.getLanglistCode();
        Map<String, List<DictionaryItem>> dataMap = dictionaryService.getItemsByCodes(new String[] {code});
        items = dataMap.get(code);
        items = null != items? items : Lists.newArrayList();
        return AjaxResult.success(items);
    }
}
