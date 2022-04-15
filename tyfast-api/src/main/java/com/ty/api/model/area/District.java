package com.ty.api.model.area;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 区县实体类
 *
 * @Author TyCode
 * @Date 2022/04/14
 */
@Data
@Accessors(chain = true)
public class District extends BaseBO {

    private static final long serialVersionUID = 27281957168312320L;

    /** 区县ID (主键) **/
    private String districtId;

    /** 城市ID **/
    private String cityId;

    /** 区县名称 **/
    private String districtName;
}
