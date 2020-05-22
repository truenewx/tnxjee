package org.truenewx.tnxjee.web.feign;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.util.WebConstants;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 请求头信息传递拦截器
 */
@Component
public class RequestHeaderDeliverInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        // 所有Feign请求均加上内部RPC请求标志
        template.header(WebConstants.HEADER_INTERNAL_RPC, Boolean.TRUE.toString());

        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            Map<String, Collection<String>> feignHeaders = template.headers();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Feign头信息中未包含的才传递，以避免Feign创建的头信息被改动
                if (!feignHeaders.containsKey(headerName)) {
                    Enumeration<String> requestHeaders = request.getHeaders(headerName);
                    Collection<String> headerValues = new ArrayList<>();
                    while (requestHeaders.hasMoreElements()) {
                        headerValues.add(requestHeaders.nextElement());
                    }
                    feignHeaders.put(headerName, headerValues);
                }
            }
            template.headers(feignHeaders);
        }
    }
}
