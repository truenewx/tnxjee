package org.truenewx.tnxjee.web.controller.spring.security.config.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

/**
 * HTTP安全配置器
 */
public abstract class HttpSecurityConfigurer
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private ApplicationContext context;

    protected final ApplicationContext getApplicationContext() {
        return this.context;
    }

    /**
     * 获取鉴权提供者实现类型，要求其实现已注册为Spring Bean。<br>
     * 当系统中包含多种鉴权方式时，必须覆写该方法指明当前鉴权场景对应的鉴权提供者实现类型
     *
     * @return
     */
    protected Class<? extends AuthenticationProvider> getAuthenticationProvider() {
        return AuthenticationProvider.class;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.authenticationProvider(this.context.getBean(getAuthenticationProvider()));
    }

}
