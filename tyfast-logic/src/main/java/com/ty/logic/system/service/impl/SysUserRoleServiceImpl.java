package com.ty.logic.system.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ty.api.model.system.SysMenu;
import com.ty.api.model.system.SysUserRole;
import com.ty.api.system.service.SysMenuService;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.SysUserRoleDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ty.cm.constant.ShiroConstant.USER_MENU_IDS;
import static com.ty.cm.constant.ShiroConstant.USER_PERMISSION;
import static com.ty.cm.constant.enums.MenuType.F;
import static com.ty.cm.constant.enums.MenuType.M;

/**
 * 用户和角色关联表业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/01/29
 */
@Service
@Transactional(readOnly = true)
public class SysUserRoleServiceImpl implements SysUserRoleService {

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 根据条件获取用户和角色关联表的总记录数
     * @param sysUserRole 用户和角色关联表
     * @return int
     */
    @Override
    public int getCount(SysUserRole sysUserRole) throws Exception {

        if (null == sysUserRole) {
            sysUserRole = new SysUserRole();
        }
        return sysUserRoleDao.findSysUserRoleCount(sysUserRole);
    }

    /**
     * 根据条件查询所有用户角色列表
     *
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     * @throws Exception
     */
    @Override
    public List<SysUserRole> getAll(SysUserRole sysUserRole) throws Exception {

        if (null == sysUserRole) {
            sysUserRole = new SysUserRole();
        }
        return sysUserRoleDao.findSysUserRole(sysUserRole);
    }

    /**
     * 查询不符合条件的所有用户角色列表
     *
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     * @throws Exception
     */
    @Override
    public List<SysUserRole> getAllNot(SysUserRole sysUserRole) throws Exception {

        if (null == sysUserRole) {
            sysUserRole = new SysUserRole();
        }
        return sysUserRoleDao.findSysUserRoleNot(sysUserRole);
    }

    /**
     * 查询授予用户的菜单和权限
     * @param sysUserRole 用户和角色关联表
     * @return List<SysUserRole>
     * @throws Exception
     */
    public List<SysUserRole> getSysUserRoleGrant(SysUserRole sysUserRole) throws Exception {

        List<SysUserRole> sysUserRoleList = Lists.newArrayList();
        if (null != sysUserRole && StringUtils.isNotBlank(sysUserRole.getUserId())) {
            sysUserRoleList = sysUserRoleDao.findSysUserRoleGrant(sysUserRole);
        }
        return sysUserRoleList;
    }

    /**
     * 获取用户的可以访问的所有菜单和权限
     *
     * @param sysUserRole 用户和角色关联表
     * @return Map<String, Set<String>>
     * @throws Exception
     */
    public Map<String, Set<String>> getMenuAndPermission(SysUserRole sysUserRole) throws Exception {

        Map<String, Set<String>> permisMap = Maps.newHashMap();
        Set<String> permissionSet = Sets.newHashSet();
        Set<String> menuIdSet = Sets.newHashSet();

        // 获取菜单和权限URL
        List<SysUserRole> sysUserRoleList = this.getSysUserRoleGrant(sysUserRole);
        if (sysUserRoleList.size() > 0) {
            for (SysUserRole userRole : sysUserRoleList) {
                SysMenu menu = userRole.getSysMenu();
                if (M.name().equalsIgnoreCase(menu.getMenuType())) {
                    menuIdSet.add(menu.getMenuId());    // 菜单ID
                    menuIdSet.add(menu.getParentId());  // 父菜单ID
                } else if (F.name().equalsIgnoreCase(menu.getMenuType())) {
                    permissionSet.add(menu.getUrl());
                }
            }
        }
        permisMap.put(USER_PERMISSION, permissionSet);
        permisMap.put(USER_MENU_IDS, menuIdSet);
        return permisMap;
    }

    /**
     * 保存用户和角色关联表数据
     *
     * @param sysUserRole 用户和角色关联表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int save(SysUserRole sysUserRole) throws Exception {

        int n = 0;
        if (null != sysUserRole) {
            sysUserRole.setUserId(UUSNUtil.nextUUSN());
            sysUserRole.setUpdateUser(sysUserRole.getCreateUser());
            n = sysUserRoleDao.saveSysUserRole(sysUserRole);
        }
        return n;
    }

    /**
     * 批量保存用户和角色关联表数据
     *
     * @param list 用户和角色关联表数据列表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int saveBatch(List<SysUserRole> list) throws Exception {

        int n = 0;
        if (null != list && list.size() > 0) {
            n = sysUserRoleDao.saveMultiSysUserRole(list);
        }
        return n;
    }

    /**
     * 删除用户和角色关联表数据
     *
     * @param sysUserRole 用户和角色关联表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(SysUserRole sysUserRole) throws Exception {

        int n = 0;
        if (null != sysUserRole && StringUtils.isNotBlank(sysUserRole.getUserId())) {
            n = sysUserRoleDao.delSysUserRole(sysUserRole);
        }
        return n;
    }

    /**
     * 根据用户ID删除用户和角色关联表数据
     *
     * @param id 用户ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(String id) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(id);
            n = this.delete(sysUserRole);
        }
        return n;
    }

    /**
     * 批量删除用户和角色关联表数据
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
            n = sysUserRoleDao.delMultiSysUserRole(ids);
        }
        return n;
    }
}
