package com.ty.logic.system.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ty.api.model.system.SysUser;
import com.ty.api.model.system.SysUserRole;
import com.ty.api.system.service.SysRoleMenuService;
import com.ty.api.system.service.SysUserRoleService;
import com.ty.api.system.service.SysUserService;
import com.ty.cm.utils.cache.Cache;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.SysUserRoleDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ty.cm.constant.ShiroConstant.ROLE;
import static com.ty.cm.constant.ShiroConstant.SESSION_TIMEOUT;
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
@Slf4j
public class SysUserRoleServiceImpl implements SysUserRoleService {

    @Autowired
    private SysUserRoleDao sysUserRoleDao;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private Cache cache;

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
            this.deleteUserRoleCache(list.get(0).getUserId());
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
            this.deleteUserRoleCache(sysUserRole.getUserId());
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

    /**
     * 查询授予用户的菜单ID
     *
     * @param roleIds   授予用户的角色ID集合
     * @return List<String>
     * @throws Exception
     */
    @Override
    public List<String> getUserMenusId(Set<String> roleIds) throws Exception {
        Set<String> menuIdList = Sets.newHashSet();
        Map<String, Map<String, Set<String>>> dataMap = this.getUserResourceGrant(roleIds);
        dataMap.values().stream().filter(item -> item.containsKey(M.name())).forEach(item -> {
            menuIdList.addAll(item.get(M.name()));

        });
        return Lists.newArrayList(menuIdList);
    }

    /**
     * 查询授予用户的权限URL
     *
     * @param roleIds   授予用户的角色ID集合
     * @return Set<String>
     * @throws Exception
     */
    @Override
    public Set<String> getUserPermission(Set<String> roleIds) throws Exception {
        Set<String> urls = Sets.newHashSet();
        Map<String, Map<String, Set<String>>> dataMap = this.getUserResourceGrant(roleIds);
        dataMap.values().stream().filter(item -> item.containsKey(F.name())).forEach(item -> {
            urls.addAll(item.get(F.name()));
        });
        return urls;
    }

    /**
     * 查询用户资源权限（含菜单ID和URL）
     */
    Map<String, Map<String, Set<String>>> getUserResourceGrant(Set<String> roleIds) throws Exception {
        Map<String, Map<String, Set<String>>> dataMap = Maps.newHashMap();
        if (null != roleIds && roleIds.size() > 0) {
            Map<String, Object> valueMap;
            // 先从Redis读取各角色的权限菜单信息
            List<String> keys = roleIds.stream().map(id -> ROLE + id).collect(Collectors.toList());
            List<String> noExistKeys = Lists.newArrayList();
            valueMap = cache.get(keys, noExistKeys);

            // 若某些角色数据Redis中没有，则从数据库查询
            if (noExistKeys.size() > 0) {
                synchronized (this) {
                    keys = noExistKeys;
                    noExistKeys = Lists.newArrayList();

                    // 再次检查Redis中，是否存在
                    valueMap.putAll(cache.get(keys, noExistKeys));

                    // 从DB查询数据
                    if (noExistKeys.size() > 0) {
                        noExistKeys = noExistKeys.stream().map(key -> key = key.substring(ROLE.length())).collect(Collectors.toList());
                        Map<String, Map<String, Set<String>>> dbDataMap = sysRoleMenuService.getMenuAndPermission(noExistKeys);

                        // 将数据放入Redis缓存
                        for (Map.Entry<String, Map<String, Set<String>>> entry : dbDataMap.entrySet()) {
                            dataMap.put(entry.getKey(), entry.getValue());
                            cache.set(ROLE + entry.getKey(), entry.getValue(), SESSION_TIMEOUT);
                        }
                    }
                }
            }

            // 数据集合并，并刷新Key有效期
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                Map<String, Set<String>> val = (Map<String, Set<String>>) entry.getValue();
                String key = entry.getKey();
                dataMap.put(key.substring(ROLE.length()), val);
                cache.touch(key, SESSION_TIMEOUT);
            }
        }
        return dataMap;
    }

    /**
     * 清除用户 Redis 中的角色数据
     */
    void deleteUserRoleCache(String userId) {
        try {
            SysUser sysUser = sysUserService.getById(userId);;
            if (null != sysUser) {
                cache.delete(sysUser.getRoleKey());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
