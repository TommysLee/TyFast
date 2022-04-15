package com.ty.api.model.area;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 市实体类
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Data
@Accessors(chain = true)
public class City extends BaseBO {

    private static final long serialVersionUID = 27281671943057408L;

    /** 城市ID (主键) **/
    private String cityId;

    /** 省ID **/
    private String provinceId;

    /** 城市名称 **/
    private String cityName;

    /** 标识(1=直辖市；2=地级市；3=县级市) **/
    private Integer flag;

    /*
     * 辅助字段
     */

    /** 省名称 **/
    private String provinceName;
}
