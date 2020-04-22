package org.truenewx.tnxjee.service.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.message.PropertiesMessageSource;

/**
 * 服务层配置
 */
@Configuration
public class ServiceConfiguration {

    @Bean({ "messageSource", "messagesSource" })
    @Primary
    public MessageSource messageSource() {
        PropertiesMessageSource messageSource = new PropertiesMessageSource();
        messageSource.setBasenames("classpath:org/hibernate/validator/ValidationMessages",
                "classpath*:META-INF/message/constant/*", "classpath*:META-INF/message/error/*",
                "classpath*:META-INF/message/info/*");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultEncoding(Strings.ENCODING_UTF8);
        messageSource.setCacheSeconds(60);
        return messageSource;
    }

}
