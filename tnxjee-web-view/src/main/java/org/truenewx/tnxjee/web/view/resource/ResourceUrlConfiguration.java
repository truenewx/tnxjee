package org.truenewx.tnxjee.web.view.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.web.view.util.WebViewPropertyConstant;

/**
 * 资源路径配置
 */
@Configuration
public class ResourceUrlConfiguration {

    private static final String DEFAULT_RESOURCES_STATIC_PATTERN = "/assets/**,/vendor/**,**/*.css,**/*.js,/robots.txt";

    @Autowired
    private Environment environment;

    @Bean("resourceUrlHandlerMapping")
    @ConditionalOnMissingBean(value = AbstractUrlHandlerMapping.class, name = "resourceUrlHandlerMapping", search = SearchStrategy.CURRENT)
    public SimpleUrlHandlerMapping resourceUrlHandlerMapping(ResourceHttpRequestHandler handler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, ResourceHttpRequestHandler> urlMap = new LinkedHashMap<>();
        String staticPattern = this.environment.getProperty(
                WebViewPropertyConstant.RESOURCES_STATIC_PATTERN, DEFAULT_RESOURCES_STATIC_PATTERN);
        String[] patterns = staticPattern.split(Strings.COMMA);
        for (String pattern : patterns) {
            pattern = pattern.trim();
            urlMap.put(pattern, handler);
        }
        mapping.setUrlMap(urlMap);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }

}
