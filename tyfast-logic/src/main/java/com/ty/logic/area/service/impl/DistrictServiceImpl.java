package com.ty.logic.area.service.impl;

import com.ty.api.area.service.DistrictService;
import com.ty.api.model.area.District;
import com.ty.cm.exception.CustomException;
import com.ty.logic.area.dao.DistrictDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ty.cm.constant.Messages.EMPTY_ID;
import static com.ty.cm.constant.Messages.EXISTS_ID;

/**
 * 区县业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Service
@Transactional(readOnly = true)
public class DistrictServiceImpl implements DistrictService {

    @Autowired
    private DistrictDao districtDao;

    /**
     * 根据条件获取区县的总记录数
     * @param district 区县
     * @return int
     */
    @Override
    public int getCount(District district) throws Exception {

        if (null == district) {
            district = new District();
        }
        return districtDao.findDistrictCount(district);
    }

    /**
     * 根据条件查询所有区县数据
     *
     * @param district 区县
     * @return List<District>
     * @throws Exception
     */
    @Override
    public List<District> getAll(District district) throws Exception {

        if (null == district) {
            district = new District();
        }
        return districtDao.findDistrict(district);
    }

    /**
     * 保存区县数据
     *
     * @param district 区县
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(District district) throws Exception {

        int n = 0;
        if (null != district && StringUtils.isNotBlank(district.getDistrictId())) {
            // ID重复校验
            if (null != getById(district.getDistrictId())) {
                throw new CustomException(EXISTS_ID);
            }

            // 保存
            n = districtDao.saveDistrict(district);
        } else {
            throw new CustomException(EMPTY_ID);
        }
        return n;
    }

    /**
     * 根据ID查询区县数据
     *
     * @param id ID
     * @return District
     * @throws Exception
     */
    @Override
    public District getById(String id) throws Exception {

        District district = null;
        if (StringUtils.isNotBlank(id)) {
            district = districtDao.findDistrictById(id);
        }
        return district;
    }

    /**
     * 更新区县数据
     *
     * @param district 区县
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(District district) throws Exception {

        int n = 0;
        if (null != district) {
            n = districtDao.updateDistrict(district);
        }
        return n;
    }

    /**
     * 删除区县数据
     *
     * @param district 区县
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(District district) throws Exception {

        int n = 0;
        if (null != district && StringUtils.isNotBlank(district.getDistrictId())) {
            n = districtDao.delDistrict(district);
        }
        return n;
    }

    /**
     * 根据ID删除区县数据
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
            District district = new District();
            district.setDistrictId(id);
            n = this.delete(district);
        }
        return n;
    }
}

