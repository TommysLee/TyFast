package com.ty.logic.system.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.api.model.system.SysRoleMenu;
import com.ty.api.system.service.SysRoleMenuService;
import com.ty.logic.system.dao.SysRoleMenuDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
            this.delete(list.get(0).getRoleId());

            // 执行批量保存
            n = sysRoleMenuDao.saveMultiSysRoleMenu(Lists.newArrayList(dataMap.values()));
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
}
