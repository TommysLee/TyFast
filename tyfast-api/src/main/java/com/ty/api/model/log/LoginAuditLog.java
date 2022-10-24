package com.ty.api.model.log;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 登录审计日志实体类
 *
 * @Author TyCode
 * @Date 2022/10/23
 */
@Data
@NoArgsConstructor
public class LoginAuditLog extends BaseBO {

    private static final long serialVersionUID = 96955407687970816L;

    public LoginAuditLog(String loginName, String ip, String userAgent, Integer type) {
        this.loginName = loginName;
        this.ip = ip;
        this.userAgent = userAgent;
        this.type = type;
    }

    /** 日志ID (主键) **/
    private String logId;

    /** 账号 **/
    private String loginName;

    /** IP地址 **/
    private String ip;

    /** 是否局域网 **/
    private Integer isLan;

    /** 浏览器UA **/
    private String userAgent;

    /** 操作系统 **/
    private String os;

    /** 类型(1=登入；2=登出) **/
    private Integer type;

    /** 日志时间 **/
    private Date logTime;
}
