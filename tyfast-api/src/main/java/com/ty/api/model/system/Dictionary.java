package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;

import java.util.List;

/**
 * 数据字典实体类
 *
 * @Author TyCode
 * @Date 2022/10/14
 */
@Data
public class Dictionary extends BaseBO {

    private static final long serialVersionUID = 93581537958391808L;

    /** 字典Code (主键) **/
    private String code;

    /** 字典名称 **/
    private String name;

    /** 字典值JSON **/
    private String items;

    /*
     * 辅助字段
     */

    /** 旧字典Code **/
    private String oldCode;

    /** 字典值集合 **/
    private List<DictionaryItem> itemList;
}
