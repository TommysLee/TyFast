package com.ty.logic.info.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.info.Information;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 新闻资讯数据访问层
 *
 * @Author TyCode
 * @Date 2026/05/23
 */
@Mapper
public interface InformationDao {

    /**
     * 根据条件查询新闻资讯记录数
     *
     * @param information 新闻资讯
     * @return int
     */
    int findInformationCount(Information information);

    /**
     * 根据条件查询所有新闻资讯数据
     *
     * @param information 新闻资讯
     * @return List<Information>
     */
    List<Information> findInformation(Information information);

    /**
     * 根据条件分页查询新闻资讯数据
     *
     * @param rowBounds 分页参数
     * @param information 新闻资讯
     * @return Page<Information>
     */
    Page<Information> findInformation(RowBounds rowBounds, Information information);

    /**
     * 根据ID查询新闻资讯数据
     *
     * @param infoId 新闻资讯ID
     * @return Information
     */
    Information findInformationById(String infoId);

    /**
     * 保存新闻资讯数据
     *
     * @param information 新闻资讯
     * @return int 返回受影响的行数
     */
    int saveInformation(Information information);

    /**
     * 更新新闻资讯数据
     *
     * @param information 新闻资讯
     * @return int 返回受影响的行数
     */
    int updateInformation(Information information);

    /**
     * 删除新闻资讯数据
     *
     * @param infoId 新闻资讯ID
     * @return int 返回受影响的行数
     */
    int delInformation(String infoId);
}
