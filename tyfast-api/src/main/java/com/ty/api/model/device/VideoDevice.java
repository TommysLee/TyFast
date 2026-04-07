package com.ty.api.model.device;

import com.ty.api.model.BaseBO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 视频设备实体类
 *
 * @Author TyCode
 * @Date 2025/08/26
 */
@Data
@Accessors(chain = true)
public class VideoDevice extends BaseBO {

    @Serial
    private static final long serialVersionUID = 473027130298167296L;

    /** 设备ID (主键) **/
    private String deviceId;

    /** 机构ID **/
    private String orgId;

    /** 设备名称 **/
    private String deviceName;

    /** 设备类别(1=萤石云) **/
    private Integer category;

    /** 设备序列号 **/
    private String serialNum;

    /** 通道号 **/
    private Integer channel;

    /** 直播HLS地址(高清) **/
    private String hlsLiveHdUrl;

    /** 直播HLS地址(流畅) **/
    private String hlsLiveUrl;

    /** 监控地址(高清) **/
    private String monitorHdUrl;

    /** 监控地址(流畅) **/
    private String monitorUrl;

    /** 回放地址(云存储) **/
    private String recCloudUrl;

    /** 回放地址(本地存储) **/
    private String recUrl;

    /** 是否启用(1=启用；0=禁用) **/
    private Integer enable;

    /** 排序号 **/
    private Integer orderNum;

    /*
     * 辅助字段
     */

    // 机构名称
    private String orgName;
}