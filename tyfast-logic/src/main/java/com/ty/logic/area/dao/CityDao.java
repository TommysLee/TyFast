package com.ty.logic.area.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.area.City;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 市数据访问层
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Mapper
public interface CityDao {

    /**
     * 根据条件查询市记录数
     * @param city 市
     * @return int
     */
    public int findCityCount(City city);

    /**
     * 根据条件查询所有市数据
     * @param city 市
     * @return List<City>
     */
    public List<City> findCity(City city);

    /**
     * 根据条件分页查询市数据
     * @param rowBounds 分页参数
     * @param city 市
     * @return Page<City>
     */
    public Page<City> findCity(RowBounds rowBounds, City city);

    /**
     * 根据ID查询市数据
     *
     * @param cityId 市ID
     * @return City
     */
    public City findCityById(String cityId);

    /**
     * 保存市数据
     *
     * @param city 市
     * @return int 返回受影响的行数
     */
    public int saveCity(City city);

    /**
     * 更新市数据
     *
     * @param city 市
     * @return int 返回受影响的行数
     */
    public int updateCity(City city);

    /**
     * 删除市数据
     *
     * @param city 市
     * @return int 返回受影响的行数
     */
    public int delCity(City city);
}
