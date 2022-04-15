package com.ty.logic.area.dao;

import com.ty.api.model.area.Province;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 省数据访问层
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Mapper
public interface ProvinceDao {

    /**
     * 根据条件查询所有省数据
     * @param province 省
     * @return List<Province>
     */
    public List<Province> findProvince(Province province);

    /**
     * 根据ID查询省数据
     *
     * @param provinceId 省ID
     * @return Province
     */
    public Province findProvinceById(String provinceId);

    /**
     * 保存省数据
     *
     * @param province 省
     * @return int 返回受影响的行数
     */
    public int saveProvince(Province province);

    /**
     * 更新省数据
     *
     * @param province 省
     * @return int 返回受影响的行数
     */
    public int updateProvince(Province province);

    /**
     * 删除省数据
     *
     * @param province 省
     * @return int 返回受影响的行数
     */
    public int delProvince(Province province);
}
