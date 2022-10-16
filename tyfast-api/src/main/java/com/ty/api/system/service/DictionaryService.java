package com.ty.api.system.service;

import com.ty.api.base.service.BaseService;
import com.ty.api.model.system.Dictionary;
import com.ty.api.model.system.DictionaryItem;

import java.util.List;
import java.util.Map;

/**
 * 数据字典业务逻辑接口
 *
 * @Author TyCode
 * @Date 2022/10/14
 */
public interface DictionaryService extends BaseService<Dictionary> {

    /**
     * 根据Code获取字典值
     *
     * @param codes Code数组
     * @return Map<String, List<DictionaryItem>>
     * @throws Exception
     */
    public Map<String, List<DictionaryItem>> getItemsByCodes(String[] codes) throws Exception;
}