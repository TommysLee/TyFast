package com.ty.logic.spring.listener;

import com.ty.api.model.system.Dictionary;
import com.ty.api.model.system.DictionaryItem;
import com.ty.api.system.service.DictionaryService;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ty.cm.constant.Numbers.NEGATIVE_1;
import static com.ty.cm.constant.Ty.CACHE_DICT_LIST;

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

    @Autowired
    @Lazy
    private Cache cache;

    /**
     * 项目启动完毕后，执行此方法
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 将全部数据字典载入Redis
        List<Dictionary> dictList = dictionaryService.getAll(null);
        Map<String, List<DictionaryItem>> dataMap = dictList.stream().map(item -> {
            item.setItemList(DataUtil.fromJSONArray(item.getItems(), DictionaryItem.class));
            item.setName(null);
            item.clean();
            return item;
        }).collect(Collectors.toMap(Dictionary::getCode, Dictionary::getItemList));
        cache.hset(CACHE_DICT_LIST, dataMap, NEGATIVE_1);
        log.info("数据字典已全部加载到Redis，共计：" + dictList.size() + " 条.");
    }
}
