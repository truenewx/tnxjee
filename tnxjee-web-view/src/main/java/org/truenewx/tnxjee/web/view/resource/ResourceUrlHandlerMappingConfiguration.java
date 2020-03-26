package org.truenewx.tnxjee.web.view.resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.truenewx.tnxjee.core.Strings;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 资源URL处理器映射配置
 */
@Configuration
@EnableConfigurationProperties(WebMvcProperties.class)
public class ResourceUrlHandlerMappingConfiguration {

    @Autowired
    private WebMvcProperties webMvcProperties;

    @Bean("resourceUrlHandlerMapping")
    @ConditionalOnMissingBean(value = AbstractUrlHandlerMapping.class, name = "resourceUrlHandlerMapping", search = SearchStrategy.CURRENT)
    public SimpleUrlHandlerMapping resourceUrlHandlerMapping(ResourceHttpRequestHandler handler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Map<String, ResourceHttpRequestHandler> urlMap = new LinkedHashMap<>();
        String staticPathPattern = this.webMvcProperties.getStaticPathPattern();
        if (StringUtils.isNotBlank(staticPathPattern)) {
            String[] staticPathPatterns = staticPathPattern.split(Strings.COMMA);
            for (String pattern : staticPathPatterns) {
                urlMap.put(pattern, handler);
            }
        }
        mapping.setUrlMap(urlMap);
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE + 2000);
        return mapping;
    }

}
