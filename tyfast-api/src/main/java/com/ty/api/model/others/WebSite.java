package com.ty.api.model.others;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Web站点信息实体类
 *
 * @Author Tommy
 * @Date 2025/9/6
 */
@Getter
@Setter
public class WebSite implements Serializable {

    @Serial
    private static final long serialVersionUID = -8177359132230081351L;

    /** 站点名称 **/
    private String title;

    /** 系统名称 **/
    private String name;

    /** LOGO **/
    private String logo;

    /** ICP备案号 **/
    private String icp;

    /** Copyright **/
    private String copyright;

    /** 站点名称::英文 **/
    private String titleEn;

    /** 站点名称::日文 **/
    private String titleJa;

    /** 系统名称::英文 **/
    private String nameEn;

    /** 系统名称::日文 **/
    private String nameJa;
}
