package com.ty.api.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑基础接口
 *
 * @Author Tommy
 * @Date 2022/1/28
 */
public interface BaseService<T> {

    /**
     * 该方法用于获取符合条件的总记录数
     *
     * @param obj 条件对象
     * @return int
     * @throws Exception
     */
    public default int getCount(T obj) throws Exception {
        return 0;
    }

    /**
     * 该方法用于查询所有数据
     *
     * @param obj 条件对象
     * @return List<T>
     * @throws Exception
     */
    public default List<T> getAll(T obj) throws Exception {
        return new ArrayList<>();
    }

    /**
     * 该方法用于数据分页查询
     *
     * @param obj 条件对象
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    public default Map<String, Object> query(T obj, String pageNum, String pageSize) throws Exception {
        return new HashMap<>();
    }

    /**
     * 该方法用于数据分页查询
     *
     * @param obj 条件对象
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<T>
     * @throws Exception
     */
    public default List<T> queryData(T obj, String pageNum, String pageSize) throws Exception {
        return new ArrayList<>();
    }

    /**
     * 该方法用于保存数据
     *
     * @param obj 数据对象
     * @return int 返回受影响的行数
     * @throws Exception
     */
    public default int save(T obj) throws Exception {
        return 0;
    }

    /**
     * 该方法用于批量保存数据
     *
     * @param list 数据列表对象
     * @return int 返回受影响的行数
     * @throws Exception
     */
    public default int saveBatch(List<T> list) throws Exception {
        return 0;
    }

    /**
     * 该方法用于查询单条数据
     *
     * @param obj 条件对象
     * @return T
     * @throws Exception
     */
    public default T getOne(T obj) throws Exception {
        return null;
    }

    /**
     * 该方法用于根据ID查询数据
     *
     * @param id ID
     * @return T
     * @throws Exception
     */
    public default T getById(String id) throws Exception {
        return null;
    }

    /**
     * 该方法用于更新数据
     *
     * @param obj 数据对象
     * @return int 返回受影响的行数
     * @throws Exception
     */
    public default int update(T obj) throws Exception {
        return 0;
    }

    /**
     * 该方法用于删除数据
     *
     * @param obj 条件的对象
     * @return int 返回受影响的行数
     * @throws Exception
     */
    public default int delete(T obj) throws Exception {
        return 0;
    }

    /**
     * 该方法用于删除数据
     *
     * @param id ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    public default int delete(String id) throws Exception {
        return 0;
    }

    /**
     * 该方法用于批量删除数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     * @throws Exception
     */
    public default int deleteBatch(List<String> ids) throws Exception {
        return 0;
    }
}
