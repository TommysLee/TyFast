package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户和角色关联表实体类
 *
 * @Author TyCode
 * @Date 2022/01/29
 */
@Data
@Accessors(chain = true)
public class SysUserRole extends BaseBO {

    private static final long serialVersionUID = 117094579138560L;

    // 用户ID (主键)
    private String userId;

    // 角色ID (主键)
    private String roleId;

    /*
     * 辅助字段
     */

    // 角色名称
    private String roleName;

    // 菜单权限实体类
    private SysMenu sysMenu;
}
