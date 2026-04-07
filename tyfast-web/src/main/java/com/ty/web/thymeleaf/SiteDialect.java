package com.ty.web.thymeleaf;

import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

/**
 * 站点基本信息表达式Dialect
 *
 * @Author Tommy
 * @Date 2025/9/5
 */
@Component
public class SiteDialect implements IExpressionObjectDialect {

    protected final IExpressionObjectFactory expressionObjectFactory;

    public SiteDialect(SiteExpressionObjectFactory siteExpressionObjectFactory) {
        this.expressionObjectFactory = siteExpressionObjectFactory;
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.expressionObjectFactory;
    }

    @Override
    public String getName() {
        return "SiteDialect";
    }
}
