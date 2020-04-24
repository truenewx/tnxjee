package org.truenewx.tnxjee.web.http.session;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;

/**
 * 会话相关Bean的提交处理器
 */
@Component
public class SessionBeanPostProcessor implements BeanPostProcessor {

    @Autowired(required = false)
    private HeaderSessionIdReader headerSessionIdReader;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (this.headerSessionIdReader != null && bean instanceof DefaultCookieSerializer) {
            return new HeaderCookieSerializer((DefaultCookieSerializer) bean, this.headerSessionIdReader);
        }
        return bean;
    }

}
