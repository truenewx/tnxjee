package org.truenewx.tnxjee.web.http.session;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;

@Component
public class CookieSerializerPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof DefaultCookieSerializer) {
            return new HeaderCookieSerializer((DefaultCookieSerializer) bean);
        }
        return bean;
    }

}
