package org.truenewx.tnxjee.web.controller;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.web.controller.http.converter.JacksonHttpMessageConverter;

/**
 * WEB控制层配置
 */
@Configuration
public class WebControllerConfiguration {

    @Bean
    public JacksonHttpMessageConverter jacksonHttpMessageConverter() {
        return new JacksonHttpMessageConverter();
    }

    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters(jacksonHttpMessageConverter());
    }

}
