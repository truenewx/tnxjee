package org.truenewx.tnxjee.web.view.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 资源URL处理器映射配置
 */
@Configuration
@EnableConfigurationProperties(ResourceProperties.class)
public class ResourceUrlHandlerMappingConfiguration {

    @Autowired
    private ResourceProperties resourceProperties;

    @Bean("resourceUrlHandlerMapping")
    @ConditionalOnMissingBean(value = AbstractUrlHandlerMapping.class, name = "resourceUrlHandlerMapping", search = SearchStrategy.CURRENT)
    public SimpleUrlHandlerMapping resourceUrlHandlerMapping(ResourceHttpRequestHandler handler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, ResourceHttpRequestHandler> urlMap = new LinkedHashMap<>();
        String[] patterns = this.resourceProperties.getStaticPatterns();
        for (String pattern : patterns) {
            urlMap.put(pattern, handler);
        }
        mapping.setUrlMap(urlMap);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return mapping;
    }

}
