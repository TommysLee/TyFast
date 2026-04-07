package com.ty.logic.system.service.impl;

import com.ty.api.model.system.ThirdAppConfig;
import com.ty.api.system.service.ThirdAppConfigService;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.system.dao.ThirdAppConfigDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 第三方应用参数配置业务逻辑实现
 *
 * @Author TyCode
 * @Date 2025/08/24
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class ThirdAppConfigServiceImpl implements ThirdAppConfigService {

    @Autowired
    private ThirdAppConfigDao thirdAppConfigDao;

    /**
     * 根据条件查询所有第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return List<ThirdAppConfig>
     * @throws Exception
     */
    @Override
    public List<ThirdAppConfig> getAll(ThirdAppConfig thirdAppConfig) throws Exception {
        if (null == thirdAppConfig) {
            thirdAppConfig = new ThirdAppConfig();
        }
        return thirdAppConfigDao.findThirdAppConfig(thirdAppConfig);
    }

    /**
     * 保存第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(ThirdAppConfig thirdAppConfig) throws Exception {
        int n = 0;
        if (null != thirdAppConfig && StringUtils.isNotBlank(thirdAppConfig.getOrgId())) {
            // 先删除改组织下的第三方应用配置
            this.delete(new ThirdAppConfig().setOrgId(thirdAppConfig.getOrgId()));

            // 再保存 “第三方应用” 配置信息
            if (StringUtils.isNotBlank(thirdAppConfig.getAppKey()) || StringUtils.isNotBlank(thirdAppConfig.getAppSecret())) {
                thirdAppConfig.setAppId(UUSNUtil.nextUUSN());
                thirdAppConfig.setUpdateUser(thirdAppConfig.getCreateUser());
                n = thirdAppConfigDao.saveThirdAppConfig(thirdAppConfig);
            }
        }
        return n;
    }

    /**
     * 根据条件查询单条第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return ThirdAppConfig
     * @throws Exception
     */
    @Override
    public ThirdAppConfig getOne(ThirdAppConfig thirdAppConfig) throws Exception {
        if (thirdAppConfig != null) {
            List<ThirdAppConfig> thirdAppConfigList = thirdAppConfigDao.findThirdAppConfig(thirdAppConfig);
            if (!thirdAppConfigList.isEmpty()) {
                return thirdAppConfigList.get(0);
            }
        }
        return null;
    }

    /**
     * 删除第三方应用参数配置数据
     *
     * @param thirdAppConfig 第三方应用参数配置
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(ThirdAppConfig thirdAppConfig) throws Exception {
        int n = 0;
        if (null != thirdAppConfig && (StringUtils.isNotBlank(thirdAppConfig.getAppId()) || StringUtils.isNotBlank(thirdAppConfig.getOrgId()))) {
            n = thirdAppConfigDao.delThirdAppConfig(thirdAppConfig);
        }
        return n;
    }

    /**
     * 根据ID删除第三方应用参数配置数据
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
            ThirdAppConfig thirdAppConfig = new ThirdAppConfig();
            thirdAppConfig.setAppId(id);
            n = this.delete(thirdAppConfig);
        }
        return n;
    }
}