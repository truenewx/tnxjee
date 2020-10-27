package org.truenewx.tnxjee.webmvc.api.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.truenewx.tnxjee.core.version.VersionReader;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger配置支持
 */
@EnableSwagger2
public abstract class SwaggerConfigSupport {

    @Autowired
    private VersionReader versionReader;
    @Value("spring.application.name")
    private String appName;

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2).groupName(this.appName).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(getBasePackage()))
                .paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        String version = this.versionReader.getVersion();
        return new ApiInfoBuilder().version(version).build();
    }

    protected abstract String getBasePackage();

}
