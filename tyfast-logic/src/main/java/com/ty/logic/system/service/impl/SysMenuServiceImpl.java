package com.ty.logic.system.service.impl;

import com.ty.api.model.system.SysMenu;
import com.ty.api.model.system.SysRoleMenu;
import com.ty.api.system.service.SysMenuService;
import com.ty.api.system.service.SysRoleMenuService;
import com.ty.cm.exception.CustomException;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.SysMenuDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ty.cm.constant.Messages.RELATED_DATA_DELETE;
import static com.ty.cm.constant.Numbers.ZERO;

/**
 * 菜单权限业务逻辑实现
 *
 * @Author TyCode
 * @Date 2022/01/29
 */
@Service
@Transactional(readOnly = true)
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuDao sysMenuDao;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 根据条件获取菜单权限的总记录数
     * @param sysMenu 菜单权限
     * @return int
     */
    @Override
    public int getCount(SysMenu sysMenu) throws Exception {

        if (null == sysMenu) {
            sysMenu = new SysMenu();
        }
        return sysMenuDao.findSysMenuCount(sysMenu);
    }

    /**
     * 根据条件查询所有菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return List<SysMenu>
     * @throws Exception
     */
    @Override
    public List<SysMenu> getAll(SysMenu sysMenu) throws Exception {

        if (null == sysMenu) {
            sysMenu = new SysMenu();
        }
        return sysMenuDao.findSysMenu(sysMenu);
    }

    /**
     * 保存菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(SysMenu sysMenu) throws Exception {

        int n = 0;
        if (null != sysMenu) {
            sysMenu.setMenuId(UUSNUtil.nextUUSN());
            sysMenu.setMenuAlias(sysMenu.getMenuName());
            sysMenu.setUpdateUser(sysMenu.getCreateUser());
            if (!String.valueOf(ZERO).equals(sysMenu.getParentId())) {
                sysMenu.setIcon(null);
            }
            n = sysMenuDao.saveSysMenu(sysMenu);
        }
        return n;
    }

    /**
     * 批量保存菜单权限数据
     *
     * @param list 菜单权限数据列表
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int saveBatch(List<SysMenu> list) throws Exception {

        int n = 0;
        if (null != list && list.size() > 0) {
            for (SysMenu sysMenu : list) {
                sysMenu.setMenuId(UUSNUtil.nextUUSN());
                sysMenu.setUpdateUser(sysMenu.getCreateUser());
            }
            n = sysMenuDao.saveMultiSysMenu(list);
        }
        return n;
    }

    /**
     * 根据条件查询单条菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return SysMenu
     * @throws Exception
     */
    @Override
    public SysMenu getOne(SysMenu sysMenu) throws Exception {

        if (sysMenu != null) {
            List<SysMenu> sysMenuList = sysMenuDao.findSysMenu(sysMenu);
            if (sysMenuList.size() > 0) {
                return sysMenuList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询菜单权限数据
     *
     * @param id ID
     * @return SysMenu
     * @throws Exception
     */
    @Override
    public SysMenu getById(String id) throws Exception {

        SysMenu sysMenu = null;
        if (StringUtils.isNotBlank(id)) {
            sysMenu = sysMenuDao.findSysMenuById(id);
        }
        return sysMenu;
    }

    /**
     * 更新菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(SysMenu sysMenu) throws Exception {

        int n = 0;
        if (null != sysMenu) {
            sysMenu.setMenuAlias(sysMenu.getMenuName());
            n = sysMenuDao.updateSysMenu(sysMenu);
        }
        return n;
    }

    /**
     * 删除菜单权限数据
     *
     * @param sysMenu 菜单权限
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(SysMenu sysMenu) throws Exception {

        int n = 0;
        if (null != sysMenu && StringUtils.isNotBlank(sysMenu.getMenuId())) {
            n = sysMenuDao.delSysMenu(sysMenu);
        }
        return n;
    }

    /**
     * 根据ID删除菜单权限数据
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

            /*
             * 判断待删除的数据，是否存在依赖关系
             */

            // 与角色是否存在关联关系
            if (sysRoleMenuService.getCount(new SysRoleMenu().setMenuId(id)) > ZERO) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            // 是否为其它菜单或权限的父级
            if (this.getCount(new SysMenu().setParentId(id)) > ZERO) {
                throw new CustomException(RELATED_DATA_DELETE);
            }

            /*
             * 执行删除操作
             */
            SysMenu sysMenu = new SysMenu();
            sysMenu.setMenuId(id);
            n = this.delete(sysMenu);
        }
        return n;
    }

    /**
     * 批量删除菜单权限数据
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
            n = sysMenuDao.delMultiSysMenu(ids);
        }
        return n;
    }
}