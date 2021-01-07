package org.truenewx.tnxjee.webmvc.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Feign;
import feign.querymap.BeanQueryMapEncoder;

@Configuration
public class FeignConfiguration {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder().queryMapEncoder(new BeanQueryMapEncoder());
    }

}
