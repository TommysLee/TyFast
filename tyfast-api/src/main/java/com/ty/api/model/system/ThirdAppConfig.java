package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 第三方应用参数配置实体类
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Data
@Accessors(chain = true)
public class ThirdAppConfig extends BaseBO {

    @Serial
    private static final long serialVersionUID = 472398281591975936L;

    /** 应用ID (主键) **/
    private String appId;

    /** 机构ID **/
    private String orgId;

    /** 应用名称 **/
    private String appName;

    /** 类别(1=萤石云) **/
    private Integer category;

    /** 应用Key **/
    private String appKey;

    /** 应用密钥 **/
    private String appSecret;
}
