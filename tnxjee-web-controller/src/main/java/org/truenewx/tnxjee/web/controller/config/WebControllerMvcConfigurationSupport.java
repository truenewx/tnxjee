package org.truenewx.tnxjee.web.controller.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.web.controller.context.request.CompositeOpenSessionInViewInterceptor;

/**
 * WEB控制层MVC配置支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebControllerMvcConfigurationSupport implements WebMvcConfigurer {

    public CompositeOpenSessionInViewInterceptor compositeOpenSessionInViewInterceptor() {
        return new CompositeOpenSessionInViewInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        CompositeOpenSessionInViewInterceptor interceptor = compositeOpenSessionInViewInterceptor();
        if (interceptor.isAvailable()) {
            registry.addWebRequestInterceptor(interceptor);
        }
    }

}
