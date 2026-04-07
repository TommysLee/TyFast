package com.ty.logic.device.dao;

import com.github.pagehelper.Page;
import com.ty.api.model.device.VideoDevice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 视频设备数据访问层
 *
 * @Author TyCode
 * @Date 2025/08/26
 */
@Mapper
public interface VideoDeviceDao {

    /**
     * 根据条件查询视频设备记录数
     *
     * @param videoDevice 视频设备
     * @return int
     */
    int findVideoDeviceCount(VideoDevice videoDevice);

    /**
     * 根据条件查询所有视频设备数据
     *
     * @param videoDevice 视频设备
     * @return List<VideoDevice>
     */
    List<VideoDevice> findVideoDevice(VideoDevice videoDevice);

    /**
     * 根据条件分页查询视频设备数据
     *
     * @param rowBounds 分页参数
     * @param videoDevice 视频设备
     * @return Page<VideoDevice>
     */
    Page<VideoDevice> findVideoDevice(RowBounds rowBounds, VideoDevice videoDevice);

    /**
     * 根据ID查询视频设备数据
     *
     * @param deviceId 视频设备ID
     * @return VideoDevice
     */
    VideoDevice findVideoDeviceById(String deviceId);

    /**
     * 保存视频设备数据
     *
     * @param videoDevice 视频设备
     * @return int 返回受影响的行数
     */
    int saveVideoDevice(VideoDevice videoDevice);

    /**
     * 更新视频设备数据
     *
     * @param videoDevice 视频设备
     * @return int 返回受影响的行数
     */
    int updateVideoDevice(VideoDevice videoDevice);

    /**
     * 删除视频设备数据
     *
     * @param videoDevice 视频设备
     * @return int 返回受影响的行数
     */
    int delVideoDevice(VideoDevice videoDevice);
}
