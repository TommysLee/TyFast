package com.ty.web.shiro;

import com.google.common.collect.Maps;
import com.ty.cm.utils.cache.Cache;
import com.ty.web.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.ty.cm.constant.ShiroConstant.ATTRIBUTES;
import static com.ty.cm.constant.ShiroConstant.REALM_NAME;
import static com.ty.cm.constant.ShiroConstant.SESSION_TIMEOUT;
import static com.ty.cm.constant.ShiroConstant.TOKEN_ID;
import static org.apache.shiro.subject.support.DefaultSubjectContext.PRINCIPALS_SESSION_KEY;
import static org.apache.shiro.web.util.WebUtils.SAVED_REQUEST_KEY;

/**
 * 分布式Session DAO
 *
 * 说明：
 * 分布式的实现依赖于 Cache抽象工厂
 * 若Cache的实现类为Redis实现，则为分布式；若为HttpSession，则为单击模式
 * 两者可以做到无缝切换，无需修改此类代码
 *
 * @Author Tommy
 * @Date 2022/1/27
 */
@Slf4j
public class DistributedSessionDao extends AbstractSessionDAO {

    @Autowired
    @Lazy
    private Cache cache;

    public DistributedSessionDao() {
        log.info("Shiro :: 启用分布式Session");
    }

    /**
     * 创建Session
     */
    @Override
    protected Serializable doCreate(Session session) {
        final Serializable sessionId = WebUtil.generateSessionId();
        assignSessionId(session, sessionId);
        storeSession(sessionId, session);
        log.info("创建Session：" + sessionId);
        return sessionId;
    }

    /**
     * 读取Session
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Session doReadSession(Serializable sessionId) {

        // 将普通的Map转换为Shiro Session
        SimpleSession session = new SimpleSession();
        session.setId(sessionId);
        Map<String, Object> sessionMap = cache.get(getKey(sessionId));
        if (null != sessionMap) {
            session.setAttributes((Map<Object, Object>) sessionMap.get(ATTRIBUTES));

            // Primary Principal
            if (sessionMap.containsKey(PRINCIPALS_SESSION_KEY)) {
                session.setAttribute(PRINCIPALS_SESSION_KEY, new SimplePrincipalCollection(sessionMap.get(PRINCIPALS_SESSION_KEY), (String) sessionMap.get(REALM_NAME)));
            }
        }
        return session;
    }

    /**
     * 更新Session
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        storeSession(session.getId(), session);
    }

    /**
     * 删除Session
     */
    @Override
    public void delete(Session session) {
        if (session == null) {
            throw new NullPointerException("session argument cannot be null.");
        }
        Serializable id = session.getId();
        if (id != null) {
            cache.deleteWithNoReply(getKey(id));
        }
        log.info("删除Session：" + session.getId() + " Timeout：" + session.getTimeout() + "ms");
    }

    /**
     * 获取所有有效Session
     */
    @Override
    public Collection<Session> getActiveSessions() {
        return Collections.emptySet();
    }

    /**
     * 存储Session
     */
    protected Session storeSession(Serializable id, Session session) {
        if (id == null) {
            throw new NullPointerException("session id argument cannot be null.");
        }
        session.removeAttribute(SAVED_REQUEST_KEY); // 移除SavedRequest

        // 将Shiro Session转换为普通的Map
        Map<String, Object> sessionMap = Maps.newHashMap();
        Map<Object, Object> attriMap = ((SimpleSession) session).getAttributes();
        if (null != attriMap && attriMap.size() > 0) {
            SimplePrincipalCollection principal = (SimplePrincipalCollection) attriMap.remove(PRINCIPALS_SESSION_KEY);
            if (null != principal && !principal.isEmpty()) {
                sessionMap.put(PRINCIPALS_SESSION_KEY, principal.getPrimaryPrincipal());
                sessionMap.put(REALM_NAME, principal.getRealmNames().iterator().next());
            }
            sessionMap.put(ATTRIBUTES, attriMap);
        }

        // 保存Session到Redis
        cache.set(getKey(id), sessionMap, SESSION_TIMEOUT);
        return session;
    }

    /**
     * 获取Session Key
     */
    protected String getKey(Serializable id) {
        return TOKEN_ID + id.toString();
    }
}
