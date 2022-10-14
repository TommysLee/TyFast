package com.ty.logic.system.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.system.Dictionary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 数据字典数据访问层
 *
 * @Author TyCode
 * @Date 2022/10/14
 */
@Mapper
public interface DictionaryDao {

    /**
     * 根据条件查询所有数据字典数据
     *
     * @param dictionary 数据字典
     * @return List<Dictionary>
     */
    public List<Dictionary> findDictionary(Dictionary dictionary);

    /**
     * 根据条件分页查询数据字典数据
     *
     * @param rowBounds 分页参数
     * @param dictionary 数据字典
     * @return Page<Dictionary>
     */
    public Page<Dictionary> findDictionary(RowBounds rowBounds, Dictionary dictionary);

    /**
     * 根据ID查询数据字典数据
     *
     * @param code 数据字典Code
     * @return Dictionary
     */
    public Dictionary findDictionaryById(String code);

    /**
     * 保存数据字典数据
     *
     * @param dictionary 数据字典
     * @return int 返回受影响的行数
     */
    public int saveDictionary(Dictionary dictionary);

    /**
     * 更新数据字典数据
     *
     * @param dictionary 数据字典
     * @return int 返回受影响的行数
     */
    public int updateDictionary(Dictionary dictionary);

    /**
     * 删除数据字典数据
     *
     * @param code 字典Code
     * @return int 返回受影响的行数
     */
    public int delDictionary(String code);
}
