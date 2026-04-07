package com.ty.web.device.controller;

import com.google.common.collect.Maps;
import com.ty.api.device.service.VideoDeviceService;
import com.ty.api.model.device.VideoDevice;
import com.ty.api.model.system.ThirdAppConfig;
import com.ty.api.system.service.ThirdAppConfigService;
import com.ty.cm.constant.Ty;
import com.ty.cm.model.AjaxResult;
import com.ty.cm.utils.HttpUtil;
import com.ty.web.base.controller.BaseController;
import com.ty.web.spring.config.properties.TyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.ty.cm.constant.Numbers.ONE;

/**
 * 视频设备Controller
 *
 * @Author TyCode
 * @Date 2025/08/26
 */
@RestController
@RequestMapping("/{orgId}/device/video")
@Slf4j
public class VideoDeviceController extends BaseController {

    @Autowired
    private VideoDeviceService videoDeviceService;

    @Autowired
    private ThirdAppConfigService thirdAppConfigService;

    @Autowired
    private TyProperties tyProperties;

    /**
     * 分页查询视频设备列表
     */
    @RequestMapping("/list")
    public AjaxResult list(VideoDevice videoDevice, @RequestParam(defaultValue = Ty.DEFAULT_PAGE) String page, @RequestParam(defaultValue = Ty.DEFAULT_PAGESIZE) String pageSize) throws Exception {
        return AjaxResult.success(videoDeviceService.query(videoDevice, page, pageSize));
    }

    /**
     * 查询机构已启用的视频设备列表
     */
    @GetMapping("/list/all")
    public AjaxResult listAll(@PathVariable String orgId) throws Exception {
        return AjaxResult.success(videoDeviceService.getAll(new VideoDevice().setOrgId(orgId).setEnable(ONE)));
    }

    /**
     * 增加视频设备
     */
    @PostMapping("/save")
    public AjaxResult save(VideoDevice videoDevice) throws Exception {
        videoDevice.setCreateUser(getCurrentUserId());
        int n = videoDeviceService.save(videoDevice);
        return AjaxResult.success(n);
    }

    /**
     * 查询视频设备明细
     */
    @GetMapping("/single/{deviceId}")
    public AjaxResult single(@PathVariable String deviceId) throws Exception {
        return AjaxResult.success(videoDeviceService.getById(deviceId));
    }

    /**
     * 修改视频设备
     */
    @PostMapping("/update")
    public AjaxResult update(VideoDevice videoDevice) throws Exception {
        videoDevice.setUpdateUser(getCurrentUserId());
        int n = videoDeviceService.update(videoDevice);
        return AjaxResult.success(n);
    }

    /**
     * 设置视频设备状态
     */
    @GetMapping("/set/status/{deviceId}/{enable}")
    public AjaxResult setStatus(@PathVariable String deviceId, @PathVariable Integer enable) throws Exception {
        VideoDevice videoDevice = new VideoDevice();
        videoDevice.setDeviceId(deviceId);
        videoDevice.setEnable(enable);
        videoDevice.setUpdateUser(getCurrentUserId());
        int n = videoDeviceService.update(videoDevice);
        return AjaxResult.success(n);
    }

    /**
     * 删除视频设备
     */
    @GetMapping("/del/{deviceId}")
    public AjaxResult del(@PathVariable String deviceId) throws Exception {
        int n = videoDeviceService.delete(deviceId);
        return AjaxResult.success(n);
    }

    /**
     * 获取萤石平台的AccessToken
     */
    @GetMapping("/accesstoken/ys")
    public AjaxResult accessTokenForYs(@PathVariable String orgId) throws Exception {
        ThirdAppConfig config = new ThirdAppConfig().setOrgId(orgId).setCategory(ONE);
        config = thirdAppConfigService.getOne(config);
        if (null != config) {
            Map<String, String> params = Maps.newHashMap();
            params.put("appKey", config.getAppKey());
            params.put("appSecret", config.getAppSecret());
            AjaxResult result = HttpUtil.postForJson(tyProperties.getYsOpenUrl(), params, HttpUtil.headers(), AjaxResult.class);
            if (null != result) {
                return AjaxResult.success(result.getData());
            } else {
                log.warn("萤石AccessToken获取失败!");
            }
        }
        return AjaxResult.success();
    }
}