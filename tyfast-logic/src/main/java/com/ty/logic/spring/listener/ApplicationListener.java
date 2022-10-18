package com.ty.logic.spring.listener;

import com.ty.api.system.service.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * SpringBoot应用启动的监听器
 *
 * @Author Tommy
 * @Date 2022/10/16
 */
@Component
@Slf4j
public class ApplicationListener implements ApplicationRunner {

    @Autowired
    private DictionaryService dictionaryService;

    /**
     * 项目启动完毕后，执行此方法
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 将数据字典载入缓存
        dictionaryService.loadCache();
    }
}
