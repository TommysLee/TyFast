package com.ty.logic.info.service.impl;

import com.github.pagehelper.Page;
import com.ty.api.info.service.InformationService;
import com.ty.api.model.info.Information;
import com.ty.cm.utils.DateUtils;
import com.ty.cm.utils.FuzzyQueryParamUtil;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.info.dao.InformationDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 新闻资讯业务逻辑实现
 *
 * @Author TyCode
 * @Date 2026/05/23
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class InformationServiceImpl implements InformationService {

    @Autowired
    private InformationDao informationDao;

    /**
     * 根据条件分页查询新闻资讯数据
     *
     * @param information 新闻资讯
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object> query(Information information, String pageNum, String pageSize) throws Exception {
        Page<Information> page = (Page<Information>) this.queryData(information, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询新闻资讯数据
     *
     * @param information 新闻资讯
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<Information> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<Information> queryData(Information information, String pageNum, String pageSize) throws Exception {
        Page<Information> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            if (null != information) {
                information.setTitle(FuzzyQueryParamUtil.escape(information.getTitle()));
            }
            page = informationDao.findInformation(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), information);
        }
        return page;
    }

    /**
     * 保存新闻资讯数据
     *
     * @param information 新闻资讯
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(Information information) throws Exception {
        int n = 0;
        if (null != information) {
            information.setInfoId(UUSNUtil.nextUUSN());
            information.setUpdateUser(information.getCreateUser());
            if (null == information.getPublishTime()) {
                information.setPublishTime(DateUtils.asLocalDate(new Date()));
            }
            n = informationDao.saveInformation(information);
        }
        return n;
    }

    /**
     * 根据ID查询新闻资讯数据
     *
     * @param id ID
     * @return Information
     * @throws Exception
     */
    @Override
    public Information getById(String id) throws Exception {
        Information information = null;
        if (StringUtils.isNotBlank(id)) {
            information = informationDao.findInformationById(id);
        }
        return information;
    }

    /**
     * 更新新闻资讯数据
     *
     * @param information 新闻资讯
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(Information information) throws Exception {
        int n = 0;
        if (null != information) {
            n = informationDao.updateInformation(information);
        }
        return n;
    }

    /**
     * 根据ID删除新闻资讯数据
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
            n = informationDao.delInformation(id);
        }
        return n;
    }
}