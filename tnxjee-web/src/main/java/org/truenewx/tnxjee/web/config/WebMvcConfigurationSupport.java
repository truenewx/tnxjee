package org.truenewx.tnxjee.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.truenewx.tnxjee.web.method.support.EscapeRequestBodyArgumentResolver;

import java.util.List;

/**
 * WEB MVC配置支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebMvcConfigurationSupport implements WebMvcConfigurer {

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new EscapeRequestBodyArgumentResolver(this.adapter));
    }

}
