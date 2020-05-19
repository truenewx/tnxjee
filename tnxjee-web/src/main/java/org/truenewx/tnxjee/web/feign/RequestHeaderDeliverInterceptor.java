package org.truenewx.tnxjee.web.feign;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                Enumeration<String> headers = request.getHeaders(headerName);
                List<String> headerValues = new ArrayList<>();
                while (headers.hasMoreElements()) {
                    headerValues.add(headers.nextElement());
                }
                template.header(headerName, headerValues);
            }
            template.header(WebConstants.HEADER_INTERNAL_RPC, Boolean.TRUE.toString());
        }
    }
}
