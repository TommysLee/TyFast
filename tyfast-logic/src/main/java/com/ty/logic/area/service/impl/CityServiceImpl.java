package com.ty.logic.area.service.impl;

import com.github.pagehelper.Page;
import com.ty.api.area.service.CityService;
import com.ty.api.area.service.DistrictService;
import com.ty.api.area.service.ProvinceService;
import com.ty.api.model.area.City;
import com.ty.api.model.area.District;
import com.ty.cm.exception.CustomException;
import com.ty.logic.area.dao.CityDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ty.cm.constant.Messages.EMPTY_ID;
import static com.ty.cm.constant.Messages.EXISTS_ID;
import static com.ty.cm.constant.Messages.RELATED_DATA_DELETE;
import static com.ty.cm.constant.Numbers.ONE;
import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 市业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Service
@Transactional(readOnly = true)
public class CityServiceImpl implements CityService {

    @Autowired
    private CityDao cityDao;

    @Autowired
    private DistrictService districtService;

    @Autowired
    @Lazy
    private ProvinceService provinceService;

    /**
     * 根据条件获取城市的总记录数
     * @param city 城市
     * @return int
     */
    @Override
    public int getCount(City city) throws Exception {

        if (null == city) {
            city = new City();
        }
        return cityDao.findCityCount(city);
    }

    /**
     * 根据条件分页查询市数据
     *
     * @param city 市
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object> query(City city, String pageNum, String pageSize) throws Exception {

        Page<City> page = (Page<City>) this.queryData(city, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询市数据
     *
     * @param city 市
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<City> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<City> queryData(City city, String pageNum, String pageSize) throws Exception {

        Page<City> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            page = cityDao.findCity(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), city);
        }
        return page;
    }

    /**
     * 保存市数据
     *
     * @param city 市
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(City city) throws Exception {

        int n = 0;
        if (null != city && StringUtils.isNotBlank(city.getCityId())) {
            // ID重复校验
            if (null != getById(city.getCityId())) {
                throw new CustomException(EXISTS_ID);
            }

            // 保存
            n = cityDao.saveCity(city);
        } else {
            throw new CustomException(EMPTY_ID);
        }
        return n;
    }

    /**
     * 根据ID查询市数据
     *
     * @param id ID
     * @return City
     * @throws Exception
     */
    @Override
    public City getById(String id) throws Exception {

        City city = null;
        if (StringUtils.isNotBlank(id)) {
            city = cityDao.findCityById(id);
        }
        return city;
    }

    /**
     * 更新市数据
     *
     * @param city 市
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(City city) throws Exception {

        int n = 0;
        if (null != city) {
            n = cityDao.updateCity(city);
        }
        return n;
    }

    /**
     * 删除市数据
     *
     * @param city 市
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(City city) throws Exception {

        int n = 0;
        if (null != city && StringUtils.isNotBlank(city.getCityId())) {
            // 若存在关联数据，则不能删除
            if (districtService.getCount(new District().setCityId(city.getCityId())) > 0) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            // 查询详情
            city = getById(city.getCityId());
            if (null != city) {
                // 执行删除
                n = cityDao.delCity(city);

                // 若为直辖市，则一并删除 Province中的数据
                if (city.getFlag().equals(ONE)) {
                    provinceService.delete(city.getProvinceId());
                }
            }
        }
        return n;
    }

    /**
     * 根据ID删除市数据
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
            City city = new City();
            city.setCityId(id);
            n = this.delete(city);
        }
        return n;
    }
}
