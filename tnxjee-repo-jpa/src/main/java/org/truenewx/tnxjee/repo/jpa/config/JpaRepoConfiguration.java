package org.truenewx.tnxjee.repo.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.truenewx.tnxjee.model.validation.config.ValidationConfigurationFactory;
import org.truenewx.tnxjee.repo.jpa.validation.config.JpaRepoValidationConfigurationFactory;

/**
 * JPA数据层配置
 */
@Configuration
public class JpaRepoConfiguration {

    @Bean
    public ValidationConfigurationFactory validationConfigurationFactory() {
        return new JpaRepoValidationConfigurationFactory();
    }

}
