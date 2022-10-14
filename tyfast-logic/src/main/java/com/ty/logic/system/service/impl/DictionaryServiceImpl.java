package com.ty.logic.system.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Sets;
import com.ty.api.model.system.Dictionary;
import com.ty.api.model.system.DictionaryItem;
import com.ty.api.system.service.DictionaryService;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.FuzzyQueryParamUtil;
import com.ty.logic.system.dao.DictionaryDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ty.cm.constant.Messages.EXISTS_DICT_CODE;
import static com.ty.cm.constant.Messages.EXISTS_DUPLICATE_ITEM_VALUE;
import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 数据字典业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/10/14
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;

    /**
     * 根据条件查询所有数据字典数据
     *
     * @param dictionary 数据字典
     * @return List<Dictionary>
     * @throws Exception
     */
    @Override
    public List<Dictionary> getAll(Dictionary dictionary) throws Exception {

        if (null == dictionary) {
            dictionary = new Dictionary();
        }
        return dictionaryDao.findDictionary(dictionary);
    }

    /**
     * 根据条件分页查询数据字典数据
     *
     * @param dictionary 数据字典
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object> query(Dictionary dictionary, String pageNum, String pageSize) throws Exception {
        if (null != dictionary) {
            dictionary.setIsLike(true); // 分页列表，使用模糊查询
            dictionary.setCode(FuzzyQueryParamUtil.escape(dictionary.getCode()));
            dictionary.setName(FuzzyQueryParamUtil.escape(dictionary.getName()));
        }
        Page<Dictionary> page = (Page<Dictionary>) this.queryData(dictionary, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询数据字典数据
     *
     * @param dictionary 数据字典
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<Dictionary> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<Dictionary> queryData(Dictionary dictionary, String pageNum, String pageSize) throws Exception {

        Page<Dictionary> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            page = dictionaryDao.findDictionary(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), dictionary);
        }
        return page;
    }

    /**
     * 保存数据字典数据
     *
     * @param dictionary 数据字典
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(Dictionary dictionary) throws Exception {

        int n = 0;
        if (null != dictionary) {
            // 字典项的Value唯一性验证
            if (!this.verifyUniqueItems(dictionary.getItems())) {
                throw new CustomException(EXISTS_DUPLICATE_ITEM_VALUE);
            }

            // 执行保存操作
            try {
                dictionary.setUpdateUser(dictionary.getCreateUser());
                n = dictionaryDao.saveDictionary(dictionary);
            } catch (DuplicateKeyException e) {
                log.warn(EXISTS_DICT_CODE + ": Code=" + dictionary.getCode());
                throw new CustomException(EXISTS_DICT_CODE);
            }
        }
        return n;
    }

    /**
     * 根据ID查询数据字典数据
     *
     * @param id ID
     * @return Dictionary
     * @throws Exception
     */
    @Override
    public Dictionary getById(String id) throws Exception {

        Dictionary dictionary = null;
        if (StringUtils.isNotBlank(id)) {
            dictionary = dictionaryDao.findDictionaryById(id);
            if (null != dictionary) {
                dictionary.setOldCode(dictionary.getCode());
            }
        }
        return dictionary;
    }

    /**
     * 更新数据字典数据
     *
     * @param dictionary 数据字典
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(Dictionary dictionary) throws Exception {

        int n = 0;
        if (null != dictionary && StringUtils.isNotBlank(dictionary.getOldCode())) {
            // 字典项的Value唯一性验证
            if (!this.verifyUniqueItems(dictionary.getItems())) {
                throw new CustomException(EXISTS_DUPLICATE_ITEM_VALUE);
            }

            // 执行更新操作
            try {
                n = dictionaryDao.updateDictionary(dictionary);
            }  catch (DuplicateKeyException e) {
                log.warn(EXISTS_DICT_CODE + ": Code=" + dictionary.getCode());
                throw new CustomException(EXISTS_DICT_CODE);
            }
        }
        return n;
    }

    /**
     * 根据ID删除数据字典数据
     *
     * @param id ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(String id) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            n = dictionaryDao.delDictionary(id);
        }
        return n;
    }

    /*
     * 验证字典的字典项的值唯一性
     */
    private boolean verifyUniqueItems(String items) {
        boolean result = true;
        if (StringUtils.isNotBlank(items)) {
            List<DictionaryItem> itemList = DataUtil.fromJSONArray(items, DictionaryItem.class);
            Set<String> valSet = Sets.newHashSet();
            for (DictionaryItem item : itemList) {
                valSet.add(item.getValue());
            }
            result = itemList.size() == valSet.size();
        }
        return result;
    }
}