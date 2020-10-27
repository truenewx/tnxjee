package org.truenewx.tnxjee.webmvc.security.config.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.truenewx.tnxjee.core.util.ClassUtil;

/**
 * 登录安全配置器支持
 *
 * @param <AP> 认证提供器实现类型
 */
public abstract class LoginSecurityConfigurerSupport<AP extends AuthenticationProvider> extends
        SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private ApplicationContext context;

    protected final ApplicationContext getApplicationContext() {
        return this.context;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        http.authenticationProvider(getAuthenticationProvider());
    }

    protected AP getAuthenticationProvider() {
        Class<AP> type = ClassUtil.getActualGenericType(getClass(), 0);
        assert type != null;
        return this.context.getBean(type);
    }

}
