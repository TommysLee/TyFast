package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 菜单权限实体类
 *
 * @Author TyCode
 * @Date 2022/01/29
 */
@Data
@Accessors(chain = true)
public class SysMenu extends BaseBO {

    @Serial
    private static final long serialVersionUID = -2287914431235614581L;

    /** 菜单ID (主键) **/
    private String menuId;

    /** 菜单名称 **/
    private String menuName;

    /** 菜单别名 **/
    private String menuAlias;

    /** 父菜单ID **/
    private String parentId;

    /** 菜单类型(M=菜单；F=功能按钮) **/
    private String menuType;

    /** 菜单图标 **/
    private String icon;

    /** 请求URL **/
    private String url;

    /** 打开方式 **/
    private String target;

    /** 菜单显隐状态(1=显示；0=隐藏) **/
    private Integer visible;

    /** 显示顺序 **/
    private Integer orderNum;
}
