package com.ty.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 业务实体类的基类
 *
 * @Author Tommy
 * @Date 2022/1/26
 */
@Data
public class BaseBO implements Serializable {

    private static final long serialVersionUID = 2788426702510961969L;

    /** 备注 **/
    private String remark;

    /** 创建者 **/
    private String createUser;

    /** 创建时间 **/
    private Date createTime;

    /** 更新者 **/
    private String updateUser;

    /** 更新时间 **/
    private Date updateTime;

    /** ID集合 **/
    private List<String> ids;

    /** 是否模糊查询 **/
    private Boolean isLike;

    /**
     * 置空不重要的属性值
     */
    public BaseBO clean() {
        this.setCreateUser(null);
        this.setCreateTime(null);
        this.setUpdateUser(null);
        this.setUpdateTime(null);
        return this;
    }
}
