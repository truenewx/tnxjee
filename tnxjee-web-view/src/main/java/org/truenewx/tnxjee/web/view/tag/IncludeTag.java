package org.truenewx.tnxjee.web.view.tag;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.NetUtil;
import org.truenewx.tnxjee.web.controller.spring.context.SpringWebContext;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlTagSupport;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

/**
 * 转调控件
 *
 * @author jianglei
 */
@Component
public class IncludeTag extends ThymeleafHtmlTagSupport {

    /**
     * 转调缓存
     */
    public static final String INCLUDE_CACHED = "_APPLICATION_INCLUDE_CACHED";

    @Override
    protected String getTagName() {
        return "include";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        ServletContext application = SpringWebContext.getServletContext();
        HttpServletRequest request = SpringWebContext.getRequest();
        String url = context.getAttributeValue("url");
        if (url.startsWith(Strings.SLASH)) {
            String host = WebViewUtil.getHost(request);
            url = request.getScheme() + "://" + host + url;
        }
        boolean cached = context.getAttributeValue("cached", Boolean.FALSE);
        Map<String, Object> attributes = context.getAttributes("url", "cached");
        try {
            String result;
            if (cached) { // 需数据缓存
                Object includeCached = application.getAttribute(INCLUDE_CACHED);
                Map<String, String> cachedMap = null;
                if (includeCached != null) {
                    cachedMap = (Map<String, String>) includeCached;
                } else {
                    cachedMap = new HashMap<>();
                }
                result = cachedMap.get(url);
                if (StringUtils.isEmpty(result)) {
                    result = NetUtil.requestByGet(url, attributes, null);
                    cachedMap.put(url, result);
                }
                application.setAttribute(INCLUDE_CACHED, cachedMap);
            } else {
                result = NetUtil.requestByGet(url, attributes, null);
            }
            handler.replaceWith(result, false);
        } catch (Throwable e) {
            // 任何异常均只打印堆栈日志，以避免影响页面整体显示
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
    }
}
