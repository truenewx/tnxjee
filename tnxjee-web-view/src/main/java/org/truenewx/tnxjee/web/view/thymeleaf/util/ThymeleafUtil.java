package org.truenewx.tnxjee.web.view.thymeleaf.util;

import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/**
 * Thymeleaf工具类
 */
public class ThymeleafUtil {

    private ThymeleafUtil() {
    }

    public static <V> V getExpressionValue(IExpressionContext context, String expression) {
        IStandardExpressionParser parser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression e = parser.parseExpression(context, expression);
        return (V) e.execute(context);
    }

}
