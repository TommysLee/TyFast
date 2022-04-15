package com.ty.logic.area.service.impl;

import com.ty.api.area.service.CityService;
import com.ty.api.area.service.ProvinceService;
import com.ty.api.model.area.City;
import com.ty.api.model.area.Province;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.FuzzyQueryParamUtil;
import com.ty.logic.area.dao.ProvinceDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ty.cm.constant.Messages.EMPTY_ID;
import static com.ty.cm.constant.Messages.EXISTS_ID;
import static com.ty.cm.constant.Messages.RELATED_DATA_DELETE;
import static com.ty.cm.constant.Numbers.ONE;

/**
 * 省业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Service
@Transactional(readOnly = true)
public class ProvinceServiceImpl implements ProvinceService {

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private CityService cityService;

    /**
     * 根据条件查询所有省数据
     *
     * @param province 省
     * @return List<Province>
     * @throws Exception
     */
    @Override
    public List<Province> getAll(Province province) throws Exception {

        if (null == province) {
            province = new Province();
        }
        province.setProvinceName(FuzzyQueryParamUtil.escape(province.getProvinceName()));
        return provinceDao.findProvince(province);
    }

    /**
     * 保存省数据
     *
     * @param province 省
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int save(Province province) throws Exception {

        int n = 0;
        if (null != province && StringUtils.isNotBlank(province.getProvinceId())) {
            // ID重复校验
            if (null != getById(province.getProvinceId())) {
                throw new CustomException(EXISTS_ID);
            }

            // 保存
            n = provinceDao.saveProvince(province);

            // 若为直辖市，则将数据复制到City表
            if (province.getFlag().equals(ONE)) {
                cityService.save(buildCity(province));
            }
        } else {
            throw new CustomException(EMPTY_ID);
        }
        return n;
    }

    /**
     * 根据ID查询省数据
     *
     * @param id ID
     * @return Province
     * @throws Exception
     */
    @Override
    public Province getById(String id) throws Exception {

        Province province = null;
        if (StringUtils.isNotBlank(id)) {
            province = provinceDao.findProvinceById(id);
        }
        return province;
    }

    /**
     * 更新省数据
     *
     * @param province 省
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int update(Province province) throws Exception {

        int n = 0;
        if (null != province) {
            n = provinceDao.updateProvince(province);

            // 若为直辖市，则同步更新City表
            if (province.getFlag().equals(ONE)) {
                cityService.update(buildCity(province));
            }
        }
        return n;
    }

    /**
     * 删除省数据
     *
     * @param province 省
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(Province province) throws Exception {

        int n = 0;
        if (null != province && StringUtils.isNotBlank(province.getProvinceId())) {
            // 若存在关联数据，则不能删除
            if (cityService.getCount(new City().setProvinceId(province.getProvinceId())) > 0) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            // 执行删除
            n = provinceDao.delProvince(province);
        }
        return n;
    }

    /**
     * 根据ID删除省数据
     *
     * @param id ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(String id) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            Province province = new Province();
            province.setProvinceId(id);
            n = this.delete(province);
        }
        return n;
    }

    /**
     * 构建直辖市对象
     */
    private City buildCity(Province province) {
        City city = new City();
        city.setCityId(province.getProvinceId());
        city.setProvinceId(province.getProvinceId());
        city.setCityName(province.getProvinceName());
        city.setFlag(ONE);
        city.setRemark(province.getRemark());
        return city;
    }
}
