package org.truenewx.tnxjee.web.http.session;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.factory.SourceBeanFactory;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * 从Header读取的sessionId读取器
 */
@Component
public class HeaderSessionIdReader {

    @Autowired
    private SourceBeanFactory beanFactory;
    private String headerName;

    public String getHeaderName() {
        if (this.headerName == null) {
            DefaultCookieSerializer cookieSerializer = this.beanFactory.getSourceBean(DefaultCookieSerializer.class);
            cookieSerializer.setUseBase64Encoding(false);
            this.headerName = BeanUtil.getFieldValue(cookieSerializer, "cookieName");
        }
        return this.headerName;
    }

    public List<String> readHeaderValues(HttpServletRequest request) {
        List<String> values = new ArrayList<>();
        Enumeration<String> headers = request.getHeaders(getHeaderName());
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (StringUtils.isNotBlank(value)) {
                values.add(value);
            }
        }
        return values;
    }

}
