package org.truenewx.tnxjee.web.view.security.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.security.config.WebSecurityConfigurerSupport;
import org.truenewx.tnxjee.web.security.web.access.BusinessExceptionAccessDeniedHandler;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * WEB视图层安全配置支持
 */
@EnableConfigurationProperties(WebMvcProperties.class)
public abstract class WebViewSecurityConfigurerSupport extends WebSecurityConfigurerSupport {

    @Autowired
    private ViewBusinessExceptionResolver viewBusinessExceptionResolver;
    @Autowired
    private WebMvcProperties mvcProperties;

    @Bean
    @Override
    public AccessDeniedHandler accessDeniedHandler() {
        BusinessExceptionAccessDeniedHandler accessDeniedHandler = (BusinessExceptionAccessDeniedHandler) super.accessDeniedHandler();
        accessDeniedHandler.setErrorPage(this.viewBusinessExceptionResolver.getErrorPath());
        return accessDeniedHandler;
    }

    @Override
    public void init(WebSecurity web) throws Exception {
        super.init(web);
        Collection<String> ignoringAntPatternCollection = getIgnoringAntPatterns();
        String[] ignoringAntPatterns = ignoringAntPatternCollection.toArray(new String[ignoringAntPatternCollection.size()]);
        web.ignoring().antMatchers(ignoringAntPatterns);
    }

    /**
     * 获取安全框架忽略的URL ANT样式集合
     *
     * @return 安全框架忽略的URL ANT样式集合
     */
    protected Collection<String> getIgnoringAntPatterns() {
        Collection<String> patterns = new HashSet<>();
        // 静态资源全部忽略
        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (StringUtils.isNotBlank(staticPathPattern)) {
            String[] staticPathPatterns = StringUtil.splitAndTrim(staticPathPattern, Strings.COMMA);
            Collections.addAll(patterns, staticPathPatterns);
        }
        return patterns;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.logout().logoutSuccessUrl(getLoginUrl());
    }
}
