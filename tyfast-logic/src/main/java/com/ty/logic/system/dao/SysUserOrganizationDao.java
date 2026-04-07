package com.ty.logic.system.dao;

import com.ty.api.model.system.SysUserOrganization;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户和机构关联表数据访问层
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Mapper
public interface SysUserOrganizationDao {

    /**
     * 根据条件查询用户和机构关联表记录数
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return int
     */
    int findSysUserOrganizationCount(SysUserOrganization sysUserOrganization);

    /**
     * 根据条件查询所有用户和机构关联表数据
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return List<SysUserOrganization>
     */
    List<SysUserOrganization> findSysUserOrganization(SysUserOrganization sysUserOrganization);

    /**
     * 保存用户和机构关联表数据
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return int 返回受影响的行数
     */
    int saveSysUserOrganization(SysUserOrganization sysUserOrganization);

    /**
     * 批量保存用户和机构关联表数据
     *
     * @param list 用户和机构关联表集合
     * @return int 返回受影响的行数
     */
    int saveMultiSysUserOrganization(List<SysUserOrganization> list);

    /**
     * 删除用户和机构关联表数据
     *
     * @param sysUserOrganization 用户和机构关联表
     * @return int 返回受影响的行数
     */
    int delSysUserOrganization(SysUserOrganization sysUserOrganization);
}
