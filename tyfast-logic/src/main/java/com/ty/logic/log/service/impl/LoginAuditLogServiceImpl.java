package com.ty.logic.log.service.impl;

import com.github.pagehelper.Page;
import com.ty.api.model.log.LoginAuditLog;
import com.ty.api.log.service.LoginAuditLogService;
import com.ty.cm.utils.IpUtils;
import com.ty.cm.utils.UserAgentUtil;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.log.dao.LoginAuditLogDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ty.cm.constant.Numbers.ONE;
import static com.ty.cm.constant.Numbers.ZERO;
import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 登录审计日志业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/10/23
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class LoginAuditLogServiceImpl implements LoginAuditLogService {

    @Autowired
    private LoginAuditLogDao loginAuditLogDao;

    /**
     * 根据条件查询所有登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @return List<LoginAuditLog>
     * @throws Exception
     */
    @Override
    public List<LoginAuditLog> getAll(LoginAuditLog loginAuditLog) throws Exception {

        if (null == loginAuditLog) {
            loginAuditLog = new LoginAuditLog();
        }
        return loginAuditLogDao.findLoginAuditLog(loginAuditLog);
    }

    /**
     * 根据条件分页查询登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object> query(LoginAuditLog loginAuditLog, String pageNum, String pageSize) throws Exception {

        Page<LoginAuditLog> page = (Page<LoginAuditLog>) this.queryData(loginAuditLog, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<LoginAuditLog> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<LoginAuditLog> queryData(LoginAuditLog loginAuditLog, String pageNum, String pageSize) throws Exception {

        Page<LoginAuditLog> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            page = loginAuditLogDao.findLoginAuditLog(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), loginAuditLog);
        }
        return page;
    }

    /**
     * 保存登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(LoginAuditLog loginAuditLog) throws Exception {

        int n = 0;
        if (null != loginAuditLog) {
            try {
                // 解析OS与IP
                loginAuditLog.setOs(UserAgentUtil.getOSName(loginAuditLog.getUserAgent()));
                loginAuditLog.setIsLan(IpUtils.internalIp(loginAuditLog.getIp())? ONE : ZERO);

                // 执行保存操作
                loginAuditLog.setLogId(UUSNUtil.nextUUSN());
                n = loginAuditLogDao.saveLoginAuditLog(loginAuditLog);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return n;
    }

    /**
     * 根据条件查询单条登录审计日志数据
     *
     * @param loginAuditLog 登录审计日志
     * @return LoginAuditLog
     * @throws Exception
     */
    @Override
    public LoginAuditLog getOne(LoginAuditLog loginAuditLog) throws Exception {

        if (loginAuditLog != null) {
            List<LoginAuditLog> loginAuditLogList = loginAuditLogDao.findLoginAuditLog(loginAuditLog);
            if (loginAuditLogList.size() > 0) {
                return loginAuditLogList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询登录审计日志数据
     *
     * @param id ID
     * @return LoginAuditLog
     * @throws Exception
     */
    @Override
    public LoginAuditLog getById(String id) throws Exception {

        LoginAuditLog loginAuditLog = null;
        if (StringUtils.isNotBlank(id)) {
            loginAuditLog = loginAuditLogDao.findLoginAuditLogById(id);
        }
        return loginAuditLog;
    }

    /**
     * 根据ID删除登录审计日志数据
     *
     * @param id ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(String id) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            n = loginAuditLogDao.delLoginAuditLog(id);
        }
        return n;
    }
}