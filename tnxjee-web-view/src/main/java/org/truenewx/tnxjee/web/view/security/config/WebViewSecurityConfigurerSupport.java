package org.truenewx.tnxjee.web.view.security.config;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;
import org.truenewx.tnxjee.web.security.config.WebSecurityConfigurerSupport;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;
import org.truenewx.tnxjee.web.view.security.authentication.logout.IgnoreAjaxLogoutSuccessHandler;

/**
 * WEB视图层安全配置支持
 */
public abstract class WebViewSecurityConfigurerSupport extends WebSecurityConfigurerSupport {

    @Autowired
    private ViewBusinessExceptionResolver viewBusinessExceptionResolver;
    @Autowired
    private WebMvcProperties mvcProperties;

    @Bean
    @Override
    public AccessDeniedHandler accessDeniedHandler() {
        AccessDeniedHandlerImpl accessDeniedHandler = (AccessDeniedHandlerImpl) super.accessDeniedHandler();
        accessDeniedHandler.setErrorPage(this.viewBusinessExceptionResolver.getErrorPath());
        return accessDeniedHandler;
    }

    /**
     * 获取安全框架忽略的URL ANT样式集合
     *
     * @return 安全框架忽略的URL ANT样式集合
     */
    @Override
    protected Collection<String> getIgnoringAntPatterns() {
        Collection<String> patterns = super.getIgnoringAntPatterns();
        // 静态资源全部忽略
        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (StringUtils.isNotBlank(staticPathPattern)) {
            String[] staticPathPatterns = StringUtil.splitAndTrim(staticPathPattern, Strings.COMMA);
            Collections.addAll(patterns, staticPathPatterns);
        }
        return patterns;
    }

    @Bean
    @Override
    public LogoutSuccessHandler logoutSuccessHandler() {
        IgnoreAjaxLogoutSuccessHandler handler = new IgnoreAjaxLogoutSuccessHandler();
        handler.setDefaultTargetUrl(getLogoutSuccessUrl());
        return handler;
    }

    /**
     * @return 登出成功后的跳转地址，默认为登录表单页面地址
     */
    protected String getLogoutSuccessUrl() {
        return getLoginFormUrl();
    }

}
