package com.ty.api.model.info;

import com.ty.api.model.BaseBO;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDate;

/**
 * 新闻资讯实体类
 *
 * @Author TyCode
 * @Date 2026/05/23
 */
@Data
public class Information extends BaseBO {

    @Serial
    private static final long serialVersionUID = 570954932812476416L;

    /** 资讯ID (主键) **/
    private String infoId;

    /** 机构ID **/
    private String orgId;

    /** 标题 **/
    private String title;

    /** 发布时间 **/
    private LocalDate publishTime;

    /** 内容 **/
    private String content;
}
