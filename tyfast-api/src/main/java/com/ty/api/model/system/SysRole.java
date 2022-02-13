package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;

/**
 * 角色实体类
 *
 * @Author TyCode
 * @Date 2022/02/03
 */
@Data
public class SysRole extends BaseBO {

    private static final long serialVersionUID = 1781642344230912L;

    // 角色ID (主键)
    private String roleId;

    // 角色名称
    private String roleName;
}
