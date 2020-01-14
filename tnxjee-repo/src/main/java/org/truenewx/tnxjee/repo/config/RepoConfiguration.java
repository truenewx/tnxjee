package org.truenewx.tnxjee.repo.config;

import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.message.PropertiesMessageSource;

/**
 * 存储层配置
 *
 * @author jianglei
 */
@Configuration
public class RepoConfiguration {

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

    @Bean("validator")
    public ValidatorFactory validatorFactory(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setProviderClass(HibernateValidator.class);
        factory.setValidationMessageSource(messageSource);
        return factory;
    }

}
