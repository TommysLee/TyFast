package com.ty.logic.log.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.log.LoginAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 登录审计日志数据访问层
 *
 * @Author TyCode
 * @Date 2022/10/23
 */
@Mapper
public interface LoginAuditLogDao {

    /**
     * 根据条件查询所有登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @return List<LoginAuditLog>
     */
    List<LoginAuditLog> findLoginAuditLog(LoginAuditLog loginAuditLog);

    /**
     * 根据条件分页查询登录审计日志数据
     *
     * @param rowBounds 分页参数
     * @param loginAuditLog 登录审计日志
     * @return Page<LoginAuditLog>
     */
    Page<LoginAuditLog> findLoginAuditLog(RowBounds rowBounds, LoginAuditLog loginAuditLog);

    /**
     * 根据ID查询登录审计日志数据
     *
     * @param logId 登录审计日志ID
     * @return LoginAuditLog
     */
    LoginAuditLog findLoginAuditLogById(String logId);

    /**
     * 保存登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @return int 返回受影响的行数
     */
    int saveLoginAuditLog(LoginAuditLog loginAuditLog);

    /**
     * 删除登录审计日志数据
     *
     * @param logId 登录审计日志ID
     * @return int 返回受影响的行数
     */
    int delLoginAuditLog(String logId);
}
