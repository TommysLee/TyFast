package com.ty.logic.system.service.impl;

import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ty.api.model.system.Dictionary;
import com.ty.api.model.system.DictionaryItem;
import com.ty.api.system.service.DictionaryService;
import com.ty.cm.constant.enums.CacheKey;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.FuzzyQueryParamUtil;
import com.ty.cm.utils.cache.Cache;
import com.ty.logic.system.dao.DictionaryDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ty.cm.constant.Messages.EXISTS_DICT_CODE;
import static com.ty.cm.constant.Messages.EXISTS_DUPLICATE_ITEM_VALUE;
import static com.ty.cm.constant.Numbers.NEGATIVE_1;
import static com.ty.cm.constant.Numbers.ZERO;
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

    @Autowired
    @Lazy
    private Cache cache;

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
            if (!this.verifyUniqueItems(dictionary)) {
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
            if (!this.verifyUniqueItems(dictionary)) {
                throw new CustomException(EXISTS_DUPLICATE_ITEM_VALUE);
            }

            // 执行更新操作
            try {
                n = dictionaryDao.updateDictionary(dictionary);

                // 同步更新Redis
                this.syncCache(dictionary);
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

            // 同步删除Redis中数据
            cache.hdelete(CacheKey.DICT_LIST.value(), id);
        }
        return n;
    }

    /**
     * 根据Code获取字典值
     *
     * @param codes Code数组
     * @return Map<String, List<DictionaryItem>>
     * @throws Exception
     */
    @Override
    public Map<String, List<DictionaryItem>> getItemsByCodes(String[] codes) throws Exception {
        Map<String, List<DictionaryItem>> dataMap = Maps.newHashMap();
        if (null != codes && codes.length > 0) {
            dataMap = cache.hget(CacheKey.DICT_LIST.value(), Lists.newArrayList(codes).stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()));
        }
        return dataMap;
    }

    /**
     * 将数据字典载入到缓存
     *
     * @return boolean
     */
    @Override
    public boolean loadCache() {
        final List<Boolean> result = Lists.newArrayList(false);
        List<Dictionary> dictList = Lists.newArrayList();
        try {
            // 获取所有数据字典
            dictList = this.getAll(null);
            Map<String, List<DictionaryItem>> dataMap = dictList.stream().map(item -> {
                item.setItemList(DataUtil.fromJSONArray(item.getItems(), DictionaryItem.class));
                item.setName(null);
                item.clean();
                return item;
            }).collect(Collectors.toMap(Dictionary::getCode, Dictionary::getItemList));

            // 载入缓存
            cache.batch(() -> {
                return result.set(ZERO, cache.hset(CacheKey.DICT_LIST.value(), dataMap, NEGATIVE_1, false));
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // 输出日志
        if (result.get(ZERO)) {
            log.info("数据字典已全部加载到Redis，共计：" + dictList.size() + " 条.");
        } else {
            log.warn("数据字典载入Redis失败!");
        }
        return result.get(ZERO);
    }

    /*
     * 验证字典的字典项的值唯一性
     */
    private boolean verifyUniqueItems(Dictionary dict) {
        boolean result = true;
        if (StringUtils.isNotBlank(dict.getItems())) {
            List<DictionaryItem> itemList = DataUtil.fromJSONArray(dict.getItems(), DictionaryItem.class);
            Set<Object> valSet = Sets.newHashSet();
            for (DictionaryItem item : itemList) {
                valSet.add(item.getValue());
            }
            dict.setItemList(itemList);
            result = itemList.size() == valSet.size();
        }
        return result;
    }

    /*
     * 将字典值缓存到Redis
     */
    private void syncCache(Dictionary dict) {
        if (null != dict && StringUtils.isNotBlank(dict.getItems())) {
            if (StringUtils.isNotBlank(dict.getOldCode())) {
                cache.hdelete(CacheKey.DICT_LIST.value(), dict.getOldCode());
            }
            cache.hset(CacheKey.DICT_LIST.value(), dict.getCode(), DataUtil.fromJSONArray(dict.getItems(), DictionaryItem.class), NEGATIVE_1);
        }
    }
}