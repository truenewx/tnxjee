package org.truenewx.tnxjee.web.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.web.controller.http.converter.JacksonHttpMessageConverter;

import java.util.List;

/**
 * WEB控制层配置，必须的控制层配置均在此配置
 */
@Configuration
public class WebControllerConfiguration implements WebMvcConfigurer {

    @Bean
    public JacksonHttpMessageConverter jacksonHttpMessageConverter() {
        return new JacksonHttpMessageConverter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jacksonHttpMessageConverter());
    }
}
