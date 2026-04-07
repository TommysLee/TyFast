package com.ty.logic.device.service.impl;

import com.github.pagehelper.Page;
import com.ty.api.model.device.VideoDevice;
import com.ty.api.device.service.VideoDeviceService;
import com.ty.cm.utils.uusn.UUSNUtil;
import com.ty.logic.device.dao.VideoDeviceDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ty.cm.constant.Ty.DATA;
import static com.ty.cm.constant.Ty.PAGES;
import static com.ty.cm.constant.Ty.TOTAL;

/**
 * 视频设备业务逻辑实现
 *
 * @Author TyCode
 * @Date 2025/08/26
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class VideoDeviceServiceImpl implements VideoDeviceService {

    @Autowired
    private VideoDeviceDao videoDeviceDao;

    /**
     * 根据条件查询所有视频设备数据
     *
     * @param videoDevice 视频设备
     * @return List<VideoDevice>
     * @throws Exception
     */
    @Override
    public List<VideoDevice> getAll(VideoDevice videoDevice) throws Exception {
        if (null == videoDevice) {
            videoDevice = new VideoDevice();
        }
        return videoDeviceDao.findVideoDevice(videoDevice);
    }

    /**
     * 根据条件分页查询视频设备数据
     *
     * @param videoDevice 视频设备
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return Map<String, Object> 返回满足条件的数据集合与记录数
     * @throws Exception
     */
    @Override
    public Map<String, Object> query(VideoDevice videoDevice, String pageNum, String pageSize) throws Exception {
        Page<VideoDevice> page = (Page<VideoDevice>) this.queryData(videoDevice, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(TOTAL, page.getTotal());
        resultMap.put(PAGES, page.getPages());
        resultMap.put(DATA, page);
        return resultMap;
    }

    /**
     * 根据条件分页查询视频设备数据
     *
     * @param videoDevice 视频设备
     * @param pageNum 页码
     * @param pageSize 每页显示条数
     * @return List<VideoDevice> 返回满足条件的数据集合
     * @throws Exception
     */
    @Override
    public List<VideoDevice> queryData(VideoDevice videoDevice, String pageNum, String pageSize) throws Exception {
        Page<VideoDevice> page = new Page<>();
        if (StringUtils.isNumeric(pageNum) && StringUtils.isNumeric(pageSize)) {
            page = videoDeviceDao.findVideoDevice(new RowBounds(Integer.parseInt(pageNum), Integer.parseInt(pageSize)), videoDevice);
        }
        return page;
    }

    /**
     * 保存视频设备数据
     *
     * @param videoDevice 视频设备
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int save(VideoDevice videoDevice) throws Exception {
        int n = 0;
        if (null != videoDevice) {
            videoDevice.setDeviceId(UUSNUtil.nextUUSN());
            videoDevice.setUpdateUser(videoDevice.getCreateUser());
            n = videoDeviceDao.saveVideoDevice(videoDevice);
        }
        return n;
    }

    /**
     * 根据条件查询单条视频设备数据
     *
     * @param videoDevice 视频设备
     * @return VideoDevice
     * @throws Exception
     */
    @Override
    public VideoDevice getOne(VideoDevice videoDevice) throws Exception {
        if (videoDevice != null) {
            List<VideoDevice> videoDeviceList = videoDeviceDao.findVideoDevice(videoDevice);
            if (!videoDeviceList.isEmpty()) {
                return videoDeviceList.get(0);
            }
        }
        return null;
    }

    /**
     * 根据ID查询视频设备数据
     *
     * @param id ID
     * @return VideoDevice
     * @throws Exception
     */
    @Override
    public VideoDevice getById(String id) throws Exception {
        VideoDevice videoDevice = null;
        if (StringUtils.isNotBlank(id)) {
            videoDevice = videoDeviceDao.findVideoDeviceById(id);
        }
        return videoDevice;
    }

    /**
     * 更新视频设备数据
     *
     * @param videoDevice 视频设备
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int update(VideoDevice videoDevice) throws Exception {
        int n = 0;
        if (null != videoDevice) {
            n = videoDeviceDao.updateVideoDevice(videoDevice);
        }
        return n;
    }

    /**
     * 删除视频设备数据
     *
     * @param videoDevice 视频设备
     * @return int 返回受影响的行数
     * @throws Exception
     */
    @Transactional
    @Override
    public int delete(VideoDevice videoDevice) throws Exception {
        int n = 0;
        if (null != videoDevice && (StringUtils.isNotBlank(videoDevice.getDeviceId()) || StringUtils.isNotBlank(videoDevice.getOrgId()))) {
            n = videoDeviceDao.delVideoDevice(videoDevice);
        }
        return n;
    }

    /**
     * 根据ID删除视频设备数据
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
            VideoDevice videoDevice = new VideoDevice();
            videoDevice.setDeviceId(id);
            n = this.delete(videoDevice);
        }
        return n;
    }
}