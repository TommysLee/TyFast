package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色和菜单关联表实体类
 *
 * @Author TyCode
 * @Date 2022/02/07
 */
@Data
@Accessors(chain = true)
public class SysRoleMenu extends BaseBO {

    private static final long serialVersionUID = 3463558038515712L;

    /** 角色ID (主键) **/
    private String roleId;

    /** 菜单ID (主键) **/
    private String menuId;

    /*
     * 辅助字段
     */

    /** 菜单权限实体类 **/
    private SysMenu sysMenu;
}
