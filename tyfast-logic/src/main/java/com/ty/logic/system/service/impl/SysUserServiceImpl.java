package com.ty.logic.system.service.impl;

import com.github.pagehelper.Page;
import com.ty.api.model.system.SysUser;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.FuzzyQueryParamUtil;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.SysUserDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ty.cm.constant.Messages.ERROR_PASSWORD;
import static com.ty.cm.constant.Messages.EXISTS_LOGIN_NAME;
import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 用户业务逻辑实现
 *
 * @Author Tommy
 * @Date 2022/1/28
 */
@Service
@Transactional(readOnly = true)
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 根据条件获取用户的总记录数
     * @param sysUser 用户
     * @return int
     */
    @Override
    public int getCount(SysUser sysUser) throws Exception {

        if (null == sysUser) {
            sysUser = new SysUser();
        }
        return sysUserDao.findSysUserCount(sysUser);
    }

    /**
     * 根据条件查询所有用户数据
     *
     * @param sysUser 用户
     * @return List<SysUser>
     * @throws Exception
     */
    @Override
    public List<SysUser> getAll(SysUser sysUser) throws Exception {

        if (null == sysUser) {
            sysUser = new SysUser();
        }
        return sysUserDao.findSysUser(sysUser);
    }

    /**
     * 根据条件分页查询用户数据
     *
     * @param sysUser 用户
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object> query(SysUser sysUser, String pageNum, String pageSize) throws Exception {

        Page<SysUser> page = (Page<SysUser>) this.queryData(sysUser, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询用户数据
     *
     * @param sysUser 用户
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<SysUser> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<SysUser> queryData(SysUser sysUser, String pageNum, String pageSize) throws Exception {

        Page<SysUser> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            sysUser.setLoginName(FuzzyQueryParamUtil.escape(sysUser.getLoginName()));
            page = sysUserDao.findSysUser(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), sysUser);
        }
        return page;
    }

    /**
     * 保存用户数据
     *
     * @param sysUser 用户
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int save(SysUser sysUser) throws Exception {

        int n = 0;
        if (null != sysUser && StringUtils.isNotBlank(sysUser.getLoginName()) && StringUtils.isNotBlank(sysUser.getPassword())) {
            // 检验账号是否存在
            SysUser param = new SysUser();
            param.setLoginName(sysUser.getLoginName());
            param.setIsLike(false);
            if (this.getCount(param) > 0) {
                throw new CustomException(EXISTS_LOGIN_NAME);
            }

            // 执行保存操作
            sysUser.setUserId(UUSNUtil.nextUUSN());
            sysUser.setSalt(UUSNUtil.javaUuid());
            sysUser.setPassword(DataUtil.encrypt(sysUser.getPassword(), sysUser.getSalt()));
            sysUser.setUpdateUser(sysUser.getCreateUser());
            n = sysUserDao.saveSysUser(sysUser);
        }
        return n;
    }

    /**
     * 批量保存用户数据
     *
     * @param list 用户数据列表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int saveBatch(List<SysUser> list) throws Exception {

        int n = 0;
        if (null != list && list.size() > 0) {
            for (SysUser sysUser : list) {
                sysUser.setUserId(UUSNUtil.nextUUSN());
                sysUser.setUpdateUser(sysUser.getCreateUser());
            }
            n = sysUserDao.saveMultiSysUser(list);
        }
        return n;
    }

    /**
     * 根据条件查询单条用户数据
     *
     * @param sysUser 用户
     * @return SysUser
     * @throws Exception
     */
    @Override
    public SysUser getOne(SysUser sysUser) throws Exception {

        if (sysUser != null) {
            List<SysUser> sysUserList = sysUserDao.findSysUser(sysUser);
            if (sysUserList.size() > 0) {
                return sysUserList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询用户数据
     *
     * @param id ID
     * @return SysUser
     * @throws Exception
     */
    @Override
    public SysUser getById(String id) throws Exception {

        SysUser sysUser = null;
        if (StringUtils.isNotBlank(id)) {
            sysUser = sysUserDao.findSysUserById(id);
        }
        return sysUser;
    }

    /**
     * 更新用户数据
     *
     * @param sysUser 用户
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int update(SysUser sysUser) throws Exception {

        int n = 0;
        if (null != sysUser) {
            // 检查账号是否被修改
            if (StringUtils.isNotBlank(sysUser.getLoginName())) {
                SysUser data = this.getById(sysUser.getUserId());
                if (!data.getLoginName().equals(sysUser.getLoginName())) {
                    // 账号被修改了，检查是否与其他用户账号冲突
                    data = new SysUser();
                    data.setLoginName(sysUser.getLoginName());
                    data.setIsLike(false);
                    List<SysUser> userList = this.getAll(data);
                    for (SysUser u : userList) {
                        if (!u.getUserId().equals(sysUser.getUserId())) {
                            throw new CustomException(EXISTS_LOGIN_NAME);
                        }
                    }
                }
            }

            // 执行更新操作
            n = sysUserDao.updateSysUser(sysUser);
        }
        return n;
    }

    /**
     * 删除用户数据
     *
     * @param sysUser 用户
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(SysUser sysUser) throws Exception {

        int n = 0;
        if (null != sysUser && StringUtils.isNotBlank(sysUser.getUserId())) {
            n = sysUserDao.delSysUser(sysUser);
        }
        return n;
    }

    /**
     * 根据ID删除用户数据
     *
     * @param id ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(String id) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            // 删除用户角色授权
            sysUserRoleService.delete(id);

            // 删除用户
            SysUser sysUser = new SysUser();
            sysUser.setUserId(id);
            n = this.delete(sysUser);
        }
        return n;
    }

    /**
     * 批量删除用户数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int deleteBatch(List<String> ids) throws Exception {

        int n = 0;
        if (null != ids && ids.size() > 0) {
            // 删除用户角色授权
            sysUserRoleService.deleteBatch(ids);

            // 删除用户
            n = sysUserDao.delMultiSysUser(ids);
        }
        return n;
    }

    /**
     * 校验原密码是否正确
     *
     * @param sysUser 用户
     * @return boolean
     * @throws Exception
     */
    @Override
    public boolean checkPassword(SysUser sysUser) throws Exception {

        boolean result = false;
        if (StringUtils.isNotBlank(sysUser.getPassword())) {
            SysUser data = this.getById(sysUser.getUserId());
            if (null != data) {
                String encryptPassword = DataUtil.encrypt(sysUser.getPassword(), data.getSalt());
                result = encryptPassword.equals(data.getPassword());
                if (!result) {
                    throw new CustomException(ERROR_PASSWORD);
                }
            }
        }
        return result;
    }

    /**
     * 修改密码
     *
     * @param sysUser 用户
     * @return boolean
     * @throws Exception
     */
    @Transactional
    @Override
    public boolean updatePassword(SysUser sysUser) throws Exception {

        boolean result = this.checkPassword(sysUser);
        if (result) {
            result = this.resetPassword(sysUser.getUserId(), sysUser.getNewPassword());
        }
        return result;
    }

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return boolean
     * @throws Exception
     */
    @Transactional
    @Override
    public boolean resetPassword(String userId, String newPassword) throws Exception {

        boolean result = false;
        if (StringUtils.isNotBlank(userId)) {
            SysUser data = new SysUser();
            data.setUserId(userId);
            data.setSalt(UUSNUtil.javaUuid());
            data.setPassword(DataUtil.encrypt(newPassword, data.getSalt()));
            result = sysUserDao.updatePassword(data) > 0;
        }
        return result;
    }
}
