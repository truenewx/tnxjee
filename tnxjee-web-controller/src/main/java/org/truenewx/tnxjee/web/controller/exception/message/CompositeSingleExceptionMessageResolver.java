package org.truenewx.tnxjee.web.controller.exception.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;
import org.truenewx.tnxjee.model.exception.SingleException;

/**
 * 复合的单异常消息解决器
 *
 * @author jianglei
 */
// 作为默认的单异常消息解决器，不要改动下列组件beanId
@Component("singleExceptionMessageResolver")
public class CompositeSingleExceptionMessageResolver
        implements SingleExceptionMessageResolver, ContextInitializedBean {

    private List<SingleExceptionMessageResolver> resolvers;

    public void setResolvers(List<SingleExceptionMessageResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        if (this.resolvers == null) {
            Map<String, SingleExceptionMessageResolver> resolvers = context
                    .getBeansOfType(SingleExceptionMessageResolver.class);
            if (!resolvers.isEmpty()) {
                this.resolvers = new ArrayList<>();
                for (SingleExceptionMessageResolver resolver : resolvers.values()) {
                    if (resolver != this) { // 防止加入自身
                        this.resolvers.add(resolver);
                    }
                }
            }
        }
    }

    @Override
    public String resolveMessage(SingleException se, Locale locale) {
        if (this.resolvers != null) {
            for (SingleExceptionMessageResolver resolver : this.resolvers) {
                String message = resolver.resolveMessage(se, locale);
                if (message != null) {
                    return message;
                }
            }
        }
        return null;
    }

}
