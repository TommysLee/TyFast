package com.ty.web.thymeleaf;

import com.ty.api.model.others.WebSite;
import com.ty.api.model.system.DictionaryItem;
import com.ty.cm.constant.enums.CacheKey;
import com.ty.cm.utils.DataUtil;
import com.ty.cm.utils.cache.Cache;
import com.ty.web.spring.SpringContextHolder;
import com.ty.web.spring.config.properties.TyProperties;
import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Thymeleaf自定义表达式：获取站点基本信息
 *
 * @Author Tommy
 * @Date 2025/9/5
 */
@Component
@Slf4j
public class SiteExpressionObjectFactory implements IExpressionObjectFactory {

    public static final String SITE_EXPRESSION_OBJECT_NAME = "site";

    protected static final Set<String> ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(
            new LinkedHashSet<>(List.of(SITE_EXPRESSION_OBJECT_NAME)
    ));

    /** 默认站点名称的Message Key **/
    public static final String TITLE_KEY = "websiteName";

    /** 默认系统名称的Message Key **/
    public static final String NAME_KEY = "productName";

    /** 默认系统LOGO **/
    public static final String LOGO = "logo-ty";

    @Autowired
    private Cache cache;

    @Autowired
    private TyProperties tyProperties;

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (SITE_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return this;
        }
        return null;
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return false;
    }

    /**
     * 获取站点名称
     *
     * @return String
     */
    public String title() {
        String title = SpringContextHolder.getMessage(TITLE_KEY);
        WebSite site = webSite();
        if (null != site) {
            String language = LocaleContextHolder.getLocale().getLanguage();
            String _title = switch (language) {
                case "en" -> site.getTitleEn();
                case "ja" -> site.getTitleJa();
                default -> site.getTitle();
            };
            if (StringUtils.isNotBlank(_title)) {
                title = _title;
            }
        }
        return title;
    }

    /**
     * 获取系统名称
     *
     * @return String
     */
    public String name() {
        String name = SpringContextHolder.getMessage(NAME_KEY);
        WebSite site = webSite();
        if (null != site) {
            String language = LocaleContextHolder.getLocale().getLanguage();
            String _name = switch (language) {
                case "en" -> site.getNameEn();
                case "ja" -> site.getNameJa();
                default -> site.getName();
            };
            if (StringUtils.isNotBlank(_name)) {
                name = _name;
            }
        }
        return name;
    }

    /**
     * 获取站点LOGO
     *
     * @return String
     */
    public String logo() {
        WebSite site = webSite();
        if (null != site && StringUtils.isNotBlank(site.getLogo())) {
            return site.getLogo();
        }
        return LOGO;
    }

    /**
     * 获取站点ICP备案号
     *
     * @return String
     */
    public String icp() {
        WebSite site = webSite();
        if (null != site && StringUtils.isNotBlank(site.getIcp())) {
            return site.getIcp();
        }
        return tyProperties.getIcp();
    }

    /**
     * 获取Copyright
     *
     * @return String
     */
    public String copyright() {
        WebSite site = webSite();
        if (null != site && StringUtils.isNotBlank(site.getCopyright())) {
            return site.getCopyright();
        }
        return tyProperties.getCopyright();
    }

    /**
     * 从缓存中获取站点信息对象
     *
     * @return WebSite
     */
    public WebSite webSite() {
        WebSite site = null;
        List<DictionaryItem> itemList = cache.hget(CacheKey.DICT_LIST.value(), tyProperties.getSiteKey());
        if (CollectionUtils.isNotEmpty(itemList)) {
            final String domain = WebUtil.getDomain();
            String siteJson = (String) itemList.stream().filter(item -> domain.equalsIgnoreCase(item.getTitle())).map(DictionaryItem::getValue).findFirst().orElse(null);
            if (StringUtils.isNotBlank(siteJson)) {
                try {
                    site = DataUtil.fromJSON(siteJson, WebSite.class);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    log.info("站点：{}, 配置：{}", domain, siteJson);
                }
            }
        }
        return site;
    }
}
