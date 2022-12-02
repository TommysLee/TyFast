package com.ty.web.spring.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.ty.web.shiro.AuthenticationFilter;
import com.ty.web.shiro.AuthorizationFilter;
import com.ty.web.shiro.DistributedSessionDao;
import com.ty.web.shiro.realm.AuthenticationRealm;
import com.ty.web.spring.config.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.config.IniFilterChainResolverFactory;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.Map;

import static com.ty.cm.constant.ShiroConstant.SESSION_TIMEOUT;
import static org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration.FILTER_NAME;
import static org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration.REGISTRATION_BEAN_NAME;

/**
 * Shiro配置
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
@Slf4j
public class ShiroConfig {

    /**
     * Shiro Realm
     */
    @Bean
    public Realm authenticationRealm() {
        return new AuthenticationRealm();
    }

    /**
     * 分布式Session Dao
     */
    @Bean
    public SessionDAO sessionDAO() {
        return new DistributedSessionDao();
    }

    /**
     * Shiro 核心过滤器
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroProperties shiroProperties) {

        final ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager); // Shiro的核心安全接口,这个属性是必须的

        // 各URL参数
        filterFactoryBean.setLoginUrl(shiroProperties.getLoginUrl()); // 登录URL,非必须的属性
        filterFactoryBean.setSuccessUrl(shiroProperties.getSuccessUrl()); // 登录成功后要跳转的URL
        filterFactoryBean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl()); // 访问未经授权的资源时,转到的URL

        // 替换Shiro默认的Filter（ShiroFilter 集成了过滤器filterchain 模式，所以Shiro内部Filter不要通过SpringBoot实例化，否则就会成为全局Filter，拦截异常）
        filterFactoryBean.getFilters().put("authc", authenticationFilter());
        filterFactoryBean.getFilters().put("perms", authorizationFilter());

        // 设置鉴权规则
        filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition(shiroProperties));

        // 设置Session有效期（只有自己实现Session DAO时，才需要设置此项）
        // 基于Servlet容器的 Shiro Session，有效期同 HttpSession
        DefaultWebSecurityManager webSecurityManager = (DefaultWebSecurityManager) securityManager;
        ((DefaultWebSessionManager) webSecurityManager.getSessionManager()).setGlobalSessionTimeout(SESSION_TIMEOUT * 1000); // 单位：毫秒

        // 设置Shiro工具类，便于获取相关对象
        SecurityUtils.setSecurityManager(securityManager);

        log.info("Apache Shiro :: 初始化完成！");
        return filterFactoryBean;
    }

    /**
     * 手动配置 Shiro 核心过滤器 (建议手动配置，否则可能因SpringBoot问题，无法初始化)
     */
    @Bean(name = REGISTRATION_BEAN_NAME)
    public FilterRegistrationBean<AbstractShiroFilter> filterShiroFilterRegistrationBean(ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {

        FilterRegistrationBean<AbstractShiroFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        filterRegistrationBean.setFilter((AbstractShiroFilter)shiroFilterFactoryBean.getObject());
        filterRegistrationBean.setName(FILTER_NAME);
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

    /**
     * Shiro连接约束配置,即过滤链的定义
     * <b>
     *  <p> anon： 匿名访问</p>
     *	<p> authc：认证访问</p>
     *	<p> perms：授权访问</p>
     *	<p> logout：注销访问</p>
     * </b>
     */
    private Map<String, String> shiroFilterChainDefinition(ShiroProperties shiroProperties) {

        final DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        if (StringUtils.isNotBlank(shiroProperties.getLoginUrl())) { // 登录
            chainDefinition.addPathDefinition(shiroProperties.getLoginUrl(), "authc");
        }
        if (StringUtils.isNotBlank(shiroProperties.getLogoutUrl())) { // 注销
            chainDefinition.addPathDefinition(shiroProperties.getLogoutUrl(), "logout");
        }

        // 设置无需鉴权的URL
        shiroProperties.getIgnoreUrls().stream().filter(url -> StringUtils.isNotBlank(url)).forEach(url -> chainDefinition.addPathDefinition(url, "anon"));

        // 读取鉴权配置信息
        if (StringUtils.isNotBlank(shiroProperties.getRules())) {
            final Ini ini = new Ini();
            ini.load(shiroProperties.getRules());
            Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
            if (CollectionUtils.isEmpty(section)) {
                section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
            }
            chainDefinition.addPathDefinitions(section);
        }

        log.info("Shiro::鉴权规则初始化完毕::DefaultShiroFilterChainDefinition! --> " + chainDefinition.getFilterChainMap());
        return chainDefinition.getFilterChainMap();
    }

    /**
     * 替换Shiro默认的Filter实现：认证过滤器
     */
    @Bean
    public AuthenticationFilter authenticationFilter() {
        AuthenticationFilter authcFilter = new AuthenticationFilter();
        authcFilter.setUsernameParam("loginName");
        return authcFilter;
    }

    /**
     * 替换Shiro默认的Filter实现：鉴权过滤器
     */
    private AuthorizationFilter authorizationFilter() {
        return new AuthorizationFilter();
    }

    /**
     * Thymeleaf 与 Shiro 整合
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }
}
