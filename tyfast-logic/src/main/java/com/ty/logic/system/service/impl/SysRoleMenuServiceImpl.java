package com.ty.logic.system.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ty.api.model.system.SysMenu;
import com.ty.api.model.system.SysRoleMenu;
import com.ty.api.system.service.SysRoleMenuService;
import com.ty.cm.utils.cache.Cache;
import com.ty.logic.system.dao.SysRoleMenuDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ty.cm.constant.ShiroConstant.ROLE;
import static com.ty.cm.constant.enums.MenuType.F;
import static com.ty.cm.constant.enums.MenuType.M;

/**
 * 角色和菜单关联表业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/02/07
 */
@Service
@Transactional(readOnly = true)
public class SysRoleMenuServiceImpl implements SysRoleMenuService {

    @Autowired
    private SysRoleMenuDao sysRoleMenuDao;

    @Autowired
    private Cache cache;

    /**
     * 根据条件获取角色和菜单关联表的总记录数
     * @param sysRoleMenu 角色和菜单关联表
     * @return int
     */
    @Override
    public int getCount(SysRoleMenu sysRoleMenu) throws Exception {

        if (null == sysRoleMenu) {
            sysRoleMenu = new SysRoleMenu();
        }
        return sysRoleMenuDao.findSysRoleMenuCount(sysRoleMenu);
    }

    /**
     * 根据条件查询所有角色和菜单关联表数据
     *
     * @param sysRoleMenu 角色和菜单关联表
     * @return List<SysRoleMenu>
     * @throws Exception
     */
    @Override
    public List<SysRoleMenu> getAll(SysRoleMenu sysRoleMenu) throws Exception {

        if (null == sysRoleMenu) {
            sysRoleMenu = new SysRoleMenu();
        }
        return sysRoleMenuDao.findSysRoleMenu(sysRoleMenu);
    }

    /**
     * 批量保存角色和菜单关联表数据
     *
     * @param list 角色和菜单关联表数据列表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int saveBatch(List<SysRoleMenu> list) throws Exception {

        int n = 0;
        if (null != list && list.size() > 0) {
            // 处理一下重复的菜单权限，防止SQL异常
            Map<String, SysRoleMenu> dataMap = Maps.newHashMap();
            for (SysRoleMenu sysRoleMenu : list) {
                dataMap.put(sysRoleMenu.getMenuId(), sysRoleMenu);
            }

            // 删除之前的角色菜单信息
            String roleId = list.get(0).getRoleId();
            this.delete(roleId);

            // 执行批量保存
            n = sysRoleMenuDao.saveMultiSysRoleMenu(Lists.newArrayList(dataMap.values()));

            // 清除Redis角色缓存
            cache.delete(ROLE + roleId);
        }
        return n;
    }

    /**
     * 删除角色和菜单关联表数据
     *
     * @param sysRoleMenu 角色和菜单关联表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(SysRoleMenu sysRoleMenu) throws Exception {

        int n = 0;
        if (null != sysRoleMenu && StringUtils.isNotBlank(sysRoleMenu.getRoleId())) {
            n = sysRoleMenuDao.delSysRoleMenu(sysRoleMenu);
        }
        return n;
    }

    /**
     * 根据ID删除角色和菜单关联表数据
     *
     * @param id 角色ID
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional(readOnly = false)
    @Override
    public int delete(String id) throws Exception {

        int n = 0;
        if (StringUtils.isNotBlank(id)) {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(id);
            n = this.delete(sysRoleMenu);
        }
        return n;
    }

    /**
     * 获取角色可以访问的所有菜单和权限
     *
     * @param roleId 角色ID
     * @return Map<String, Set<String>>
     * @throws Exception
     */
    @Override
    public Map<String, Set<String>> getMenuAndPermission(String roleId) throws Exception {
        Map<String, Set<String>> menuPermisMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(roleId)) {
            Map<String, Map<String, Set<String>>> dataMap = this.getMenuAndPermission(Lists.newArrayList(roleId));
            if (dataMap.containsKey(roleId)) {
                menuPermisMap = dataMap.get(roleId);
            }
        }
        return menuPermisMap;
    }

    /**
     * 获取角色可以访问的所有菜单和权限
     *
     * @param roleIdList 角色ID列表
     * @return Map<String, Map<String, Set<String>>>
     * @throws Exception
     */
    @Override
    public Map<String, Map<String, Set<String>>> getMenuAndPermission(List<String> roleIdList) throws Exception {
        Map<String, Map<String, Set<String>>> dataMap = Maps.newHashMap();
        if (null != roleIdList && roleIdList.size() > 0) {
            List<SysRoleMenu> dataList = sysRoleMenuDao.findSysRoleMenuGrant(roleIdList);
            if (dataList.size() > 0) {
                // 根据角色ID分组
                Map<String, List<SysRoleMenu>> dataGroupMap = dataList.stream().collect(Collectors.groupingBy(SysRoleMenu::getRoleId));

                // 逐一获取每个角色的菜单和权限URL
                for (Map.Entry<String, List<SysRoleMenu>> entry : dataGroupMap.entrySet()) {
                    Set<String> permissionSet = Sets.newHashSet();
                    Set<String> menuIdSet = Sets.newHashSet();
                    for (SysRoleMenu roleMenu : entry.getValue()) {
                        SysMenu menu = roleMenu.getSysMenu();
                        if (M.name().equalsIgnoreCase(menu.getMenuType())) {
                            menuIdSet.add(roleMenu.getMenuId()); // 菜单ID
                            menuIdSet.add(menu.getParentId());   // 父菜单ID
                        } else if (F.name().equalsIgnoreCase(menu.getMenuType())) {
                            permissionSet.add(menu.getUrl());    // 权限URL
                        }
                    }
                    Map<String, Set<String>> roleMap = Maps.newHashMap();
                    roleMap.put(M.name(), menuIdSet);
                    roleMap.put(F.name(), permissionSet);
                    dataMap.put(entry.getKey(), roleMap);
                }
            }
        }
        return dataMap;
    }
}
