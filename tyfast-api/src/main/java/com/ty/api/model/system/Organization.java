package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 组织机构实体类
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Data
@Accessors(chain = true)
public class Organization extends BaseBO {

    @Serial
    private static final long serialVersionUID = 472269624550584320L;

    /** 机构ID (主键) **/
    private String orgId;

    /** 上级机构ID（0=根节点） **/
    private String parentId;

    /** 机构编码 **/
    private String orgCode;

    /** 机构名称 **/
    private String orgName;

    /** 省ID **/
    private String provinceId;

    /** 省名称 **/
    private String provinceName;

    /** 城市ID **/
    private String cityId;

    /** 城市名称 **/
    private String cityName;

    /** 经度 **/
    private Double lng;

    /** 纬度 **/
    private Double lat;
}

