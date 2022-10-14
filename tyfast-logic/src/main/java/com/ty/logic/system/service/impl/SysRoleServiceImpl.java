package com.ty.logic.system.service.impl;

import com.github.pagehelper.Page;
import com.ty.api.model.system.SysRole;
import com.ty.api.model.system.SysUserRole;
import com.ty.api.system.service.SysRoleMenuService;
import com.ty.api.system.service.SysRoleService;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.FuzzyQueryParamUtil;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.SysRoleDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ty.cm.constant.Messages.RELATED_DATA_DELETE;
import static com.ty.cm.constant.Numbers.ZERO;
import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 角色业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/02/03
 */
@Service
@Transactional(readOnly = true)
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleDao sysRoleDao;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 根据条件查询所有角色数据
     *
     * @param sysRole 角色
     * @return List<SysRole>
     * @throws Exception
     */
    @Override
    public List<SysRole> getAll(SysRole sysRole) throws Exception {

        if (null == sysRole) {
            sysRole = new SysRole();
        }
        return sysRoleDao.findSysRole(sysRole);
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param sysRole 角色
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object>  query(SysRole sysRole, String pageNum, String pageSize) throws Exception {

        Page<SysRole> page = (Page<SysRole>) this.queryData(sysRole, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param sysRole 角色
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<SysRole> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<SysRole> queryData(SysRole sysRole, String pageNum, String pageSize) throws Exception {

        Page<SysRole> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            sysRole.setRoleName(FuzzyQueryParamUtil.escape(sysRole.getRoleName()));
            page = sysRoleDao.findSysRole(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), sysRole);
        }
        return page;
    }

    /**
     * 保存角色数据
     *
     * @param sysRole 角色
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(SysRole sysRole) throws Exception {

        int n = 0;
        if (null != sysRole) {
            sysRole.setRoleId(UUSNUtil.nextUUSN());
            sysRole.setUpdateUser(sysRole.getCreateUser());
            n = sysRoleDao.saveSysRole(sysRole);
        }
        return n;
    }

    /**
     * 批量保存角色数据
     *
     * @param list 角色数据列表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int saveBatch(List<SysRole> list) throws Exception {

        int n = 0;
        if (null != list && list.size() > 0) {
            for (SysRole sysRole : list) {
                sysRole.setRoleId(UUSNUtil.nextUUSN());
                sysRole.setUpdateUser(sysRole.getCreateUser());
            }
            n = sysRoleDao.saveMultiSysRole(list);
        }
        return n;
    }

    /**
     * 根据条件查询单条角色数据
     *
     * @param sysRole 角色
     * @return SysRole
     * @throws Exception
     */
    @Override
    public SysRole getOne(SysRole sysRole) throws Exception {

        if (sysRole != null) {
            List<SysRole> sysRoleList = sysRoleDao.findSysRole(sysRole);
            if (sysRoleList.size() > 0) {
                return sysRoleList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询角色数据
     *
     * @param id ID
     * @return SysRole
     * @throws Exception
     */
    @Override
    public SysRole getById(String id) throws Exception {

        SysRole sysRole = null;
        if (StringUtils.isNotBlank(id)) {
            sysRole = sysRoleDao.findSysRoleById(id);
        }
        return sysRole;
    }

    /**
     * 更新角色数据
     *
     * @param sysRole 角色
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(SysRole sysRole) throws Exception {

        int n = 0;
        if (null != sysRole) {
            n = sysRoleDao.updateSysRole(sysRole);
        }
        return n;
    }

    /**
     * 删除角色数据
     *
     * @param sysRole 角色
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(SysRole sysRole) throws Exception {

        int n = 0;
        if (null != sysRole && StringUtils.isNotBlank(sysRole.getRoleId())) {

            /*
             * 判断待删除的数据，是否存在依赖关系
             */

            // 与用户是否存在关联关系
            if (sysUserRoleService.getCount(new SysUserRole().setRoleId(sysRole.getRoleId())) > ZERO) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            /*
             * 执行删除操作
             */

            // 删除角色和菜单关联表数据
            sysRoleMenuService.delete(sysRole.getRoleId());

            // 删除角色
            n = sysRoleDao.delSysRole(sysRole);
        }
        return n;
    }

    /**
     * 根据ID删除角色数据
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
            SysRole sysRole = new SysRole();
            sysRole.setRoleId(id);
            n = this.delete(sysRole);
        }
        return n;
    }

    /**
     * 批量删除角色数据
     *
     * @param ids ID集合
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int deleteBatch(List<String> ids) throws Exception {

        int n = 0;
        if (null != ids && ids.size() > 0) {
            n = sysRoleDao.delMultiSysRole(ids);
        }
        return n;
    }
}