package org.truenewx.tnxjee.web.controller.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.truenewx.tnxjee.web.controller.http.converter.JsonHttpMessageConverter;

/**
 * WEB控制层MVC配置支持
 *
 * @author jianglei
 */
public class WebControllerMvcConfigurationSupport {

    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters(new JsonHttpMessageConverter());
    }

}
