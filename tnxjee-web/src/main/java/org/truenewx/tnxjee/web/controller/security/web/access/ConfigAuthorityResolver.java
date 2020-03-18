package org.truenewx.tnxjee.web.controller.security.web.access;

import org.springframework.http.HttpMethod;
import org.truenewx.tnxjee.model.spec.user.security.UserConfigAuthority;

import java.util.Collection;

/**
 * 配置权限解决器
 */
public interface ConfigAuthorityResolver {

    /**
     * 解决指定请求访问所需的配置权限集
     *
     * @param uri    请求路径
     * @param method 请求方法
     * @return 配置权限集
     */
    Collection<UserConfigAuthority> resolveConfigAuthorities(String uri, HttpMethod method);

}
