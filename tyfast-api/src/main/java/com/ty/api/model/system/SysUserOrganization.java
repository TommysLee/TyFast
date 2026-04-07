package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 用户和机构关联表实体类
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Data
@Accessors(chain = true)
public class SysUserOrganization extends BaseBO {

    @Serial
    private static final long serialVersionUID = 472273553271255040L;

    /** 用户ID (主键) **/
    private String userId;

    /** 机构ID (主键) **/
    private String orgId;

    /** 是否默认机构(0=否;1=是) **/
    private Integer isDefault;
}
