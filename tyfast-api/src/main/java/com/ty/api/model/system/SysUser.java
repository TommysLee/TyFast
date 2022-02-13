package com.ty.api.model.system;

import com.ty.api.model.BaseBO;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 *
 * @Author Tommy
 * @Date 2022/1/28
 */
@Data
public class SysUser extends BaseBO {

    private static final long serialVersionUID = 8757373350151986521L;

    // 用户ID
    private String userId;

    // 账号
    private String loginName;

    // 用户类型(1=系统用户；2=普通用户)
    private Integer userType;

    // 姓名
    private String realName;

    // 性别(1=男；0=女)
    private Integer sex;

    // 手机
    private String phone;

    // 邮箱
    private String email;

    // 密码
    private String password;

    // 盐密码
    private String salt;

    // 账号状态(0=正常；1=冻结)
    private Integer status;

    // 默认主页
    private String homeAction;

    // 最后登录IP
    private String loginIp;

    // 最后登录时间
    private Date loginTime;

    /*
     * 辅助字段
     */

    // 是否模糊查询
    private Boolean isLike;

    // 新密码
    private String newPassword;

    /**
     * 置空敏感属性或不重要的属性
     */
    @Override
    public SysUser clean() {
        super.clean();
        this.setRemark(null);
        this.setPassword(null);
        this.setSalt(null);
        return this;
    }
}
