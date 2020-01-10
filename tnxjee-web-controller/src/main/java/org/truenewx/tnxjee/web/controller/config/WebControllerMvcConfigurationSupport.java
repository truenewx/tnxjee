package org.truenewx.tnxjee.web.controller.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.truenewx.tnxjee.web.controller.http.converter.JsonHttpMessageConverter;

/**
 * WEB控制层MVC配置支持
 *
 * @author jianglei
 */
public abstract class WebControllerMvcConfigurationSupport {

    @Bean
    public HttpMessageConverter<Object> httpMessageConverter() {
        return new JsonHttpMessageConverter();
    }

    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters(httpMessageConverter());
    }

}
