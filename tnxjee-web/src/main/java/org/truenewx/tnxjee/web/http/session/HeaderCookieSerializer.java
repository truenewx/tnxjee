package org.truenewx.tnxjee.web.http.session;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * 从Cookie取不到则从Header取的sessionId序列化器
 */
public class HeaderCookieSerializer extends DefaultCookieSerializer {

    private DefaultCookieSerializer delegate;
    private String headerName;

    public HeaderCookieSerializer(DefaultCookieSerializer delegate) {
        this.delegate = delegate;
    }

    public String getHeaderName() {
        if (this.headerName == null) {
            this.headerName = BeanUtil.getFieldValue(this.delegate, "cookieName");
            this.delegate.setUseBase64Encoding(false); // 只能支持不加密的
        }
        return this.headerName;
    }

    @Override
    public List<String> readCookieValues(HttpServletRequest request) {
        String headerName = getHeaderName(); // 优先确保配置正确，从第2次调用开始性能损耗可以忽略不计
        List<String> values = this.delegate.readCookieValues(request);
        if (values.isEmpty()) {
            Enumeration<String> headers = request.getHeaders(headerName);
            while (headers.hasMoreElements()) {
                String value = headers.nextElement();
                if (StringUtils.isNotBlank(value)) {
                    values.add(value);
                }
            }
        }
        return values;
    }

    @Override
    public void writeCookieValue(CookieValue cookieValue) {
        this.delegate.writeCookieValue(cookieValue);
    }

    @Override
    public void setUseSecureCookie(boolean useSecureCookie) {
        this.delegate.setUseSecureCookie(useSecureCookie);
    }

    @Override
    public void setUseHttpOnlyCookie(boolean useHttpOnlyCookie) {
        this.delegate.setUseHttpOnlyCookie(useHttpOnlyCookie);
    }

    @Override
    public void setCookiePath(String cookiePath) {
        this.delegate.setCookiePath(cookiePath);
    }

    @Override
    public void setCookieName(String cookieName) {
        this.delegate.setCookieName(cookieName);
    }

    @Override
    public void setCookieMaxAge(int cookieMaxAge) {
        this.delegate.setCookieMaxAge(cookieMaxAge);
    }

    @Override
    public void setDomainName(String domainName) {
        this.delegate.setDomainName(domainName);
    }

    @Override
    public void setDomainNamePattern(String domainNamePattern) {
        this.delegate.setDomainNamePattern(domainNamePattern);
    }

    @Override
    public void setJvmRoute(String jvmRoute) {
        this.delegate.setJvmRoute(jvmRoute);
    }

    @Override
    public void setUseBase64Encoding(boolean useBase64Encoding) {
        this.delegate.setUseBase64Encoding(false); // 只能支持不加密的
    }

    @Override
    public void setRememberMeRequestAttribute(String rememberMeRequestAttribute) {
        this.delegate.setRememberMeRequestAttribute(rememberMeRequestAttribute);
    }

    @Override
    public void setSameSite(String sameSite) {
        this.delegate.setSameSite(sameSite);
    }
}
