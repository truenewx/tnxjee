package org.truenewx.tnxjee.web.view.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.view.tagext.SimpleDynamicAttributeTagSupport;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

/**
 * 转调控件
 *
 * @author jianglei
 */
public class IncludeTag extends SimpleDynamicAttributeTagSupport {

    /**
     * 转调缓存
     */
    public static final String INCLUDE_CACHED = "_APPLICATION_INCLUDE_CACHED";

    /**
     * 是否缓存
     */
    private boolean cached;

    /**
     * 图片链接
     */
    private String url;

    /**
     * @param url 图片链接
     *
     * @author jianglei
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    public void setCached(final boolean cached) {
        this.cached = cached;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doTag() throws JspException, IOException {
        final ServletContext application = getPageContext().getServletContext();
        final ServletRequest request = getPageContext().getRequest();
        if (this.url.substring(0, 1).equals(Strings.SLASH)) {
            final String host = WebViewUtil.getHost((HttpServletRequest) request);
            this.url = request.getScheme() + "://" + host + this.url;
        }
        try {
            if (this.cached) { // 需数据缓存
                final Object includeCached = application.getAttribute(INCLUDE_CACHED);
                Map<String, String> casheMap = null;
                if (includeCached != null) {
                    casheMap = (Map<String, String>) includeCached;
                } else {
                    casheMap = new HashMap<>();
                }
                String result = casheMap.get(this.url);
                if (StringUtils.isEmpty(result)) {
                    result = NetUtil.requestByGet(this.url, this.attributes, null);
                    casheMap.put(this.url, result);
                }
                print(result);
                application.setAttribute(INCLUDE_CACHED, casheMap);
            } else {
                print(NetUtil.requestByGet(this.url, this.attributes, null));
            }
        } catch (final Throwable e) {
            // 任何异常均只打印堆栈日志，以避免影响页面整体显示
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }
}
