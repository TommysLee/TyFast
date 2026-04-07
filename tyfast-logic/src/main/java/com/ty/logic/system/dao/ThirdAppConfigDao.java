package com.ty.logic.system.dao;

import com.ty.api.model.system.ThirdAppConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 第三方应用参数配置数据访问层
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Mapper
public interface ThirdAppConfigDao {

    /**
     * 根据条件查询所有第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return List<ThirdAppConfig>
     */
    List<ThirdAppConfig> findThirdAppConfig(ThirdAppConfig thirdAppConfig);

    /**
     * 保存第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return int 返回受影响的行数
     */
    int saveThirdAppConfig(ThirdAppConfig thirdAppConfig);

    /**
     * 删除第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return int 返回受影响的行数
     */
    int delThirdAppConfig(ThirdAppConfig thirdAppConfig);
}
