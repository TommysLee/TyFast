package com.ty.api.model.system;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典项实体类
 *
 * @Author TyCode
 * @Date 2022/10/14
 */
@Data
public class DictionaryItem implements Serializable {

    private static final long serialVersionUID = 95581537958391808L;

    /** Item值 **/
    private String value;

    /** Item名称 **/
    private String name;
}
