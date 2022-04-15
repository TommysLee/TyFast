package com.ty.api.model.area;

import com.ty.api.model.BaseBO;
import lombok.Data;

/**
 * 省实体类
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Data
public class Province extends BaseBO {

    private static final long serialVersionUID = 27280986761555968L;

    /** 省ID (主键) **/
    private String provinceId;

    /** 省名称 **/
    private String provinceName;

    /** 标识(1=直辖市；2=省；3=自治区；4=特别行政区) **/
    private Integer flag;
}
