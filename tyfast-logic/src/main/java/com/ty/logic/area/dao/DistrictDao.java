package com.ty.logic.area.dao;

import com.ty.api.model.area.District;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 区县数据访问层
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Mapper
public interface DistrictDao {

    /**
     * 根据条件查询区县记录数
     * @param district 区县
     * @return int
     */
    public int findDistrictCount(District district);

    /**
     * 根据条件查询所有区县数据
     * @param district 区县
     * @return List<District>
     */
    public List<District> findDistrict(District district);

    /**
     * 根据ID查询区县数据
     *
     * @param districtId 区县ID
     * @return District
     */
    public District findDistrictById(String districtId);

    /**
     * 保存区县数据
     *
     * @param district 区县
     * @return int 返回受影响的行数
     */
    public int saveDistrict(District district);

    /**
     * 更新区县数据
     *
     * @param district 区县
     * @return int 返回受影响的行数
     */
    public int updateDistrict(District district);

    /**
     * 删除区县数据
     *
     * @param district 区县
     * @return int 返回受影响的行数
     */
    public int delDistrict(District district);
}
