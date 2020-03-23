package org.truenewx.tnxjee.web.http.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.web.http.converter.JacksonHttpMessageConverter;

import java.util.List;

@Configuration
public class WebHttpConfigurer implements WebMvcConfigurer {

    @Bean
    public JacksonHttpMessageConverter jacksonHttpMessageConverter() {
        return new JacksonHttpMessageConverter();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jacksonHttpMessageConverter());
    }

}
