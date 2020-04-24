package org.truenewx.tnxjee.web.http.session;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.truenewx.tnxjee.core.util.BeanUtil;

/**
 * 可从Header取的sessionId序列化器
 */
public class HeaderCookieSerializer implements CookieSerializer {

    private DefaultCookieSerializer delegate;
    private HeaderSessionIdReader headerReader;

    public HeaderCookieSerializer(DefaultCookieSerializer delegate,
            HeaderSessionIdReader headerReader) {
        delegate.setUseBase64Encoding(false); // 只能支持不编码
        headerReader.setHeaderName(BeanUtil.getFieldValue(delegate, "cookieName"));
        this.delegate = delegate;
        this.headerReader = headerReader;
    }

    @Override
    public List<String> readCookieValues(HttpServletRequest request) {
        List<String> values = this.headerReader.readSessionIds(request);
        values.addAll(this.delegate.readCookieValues(request));
        return values;
    }

    @Override
    public void writeCookieValue(CookieValue cookieValue) {
        this.delegate.writeCookieValue(cookieValue);
    }

}
