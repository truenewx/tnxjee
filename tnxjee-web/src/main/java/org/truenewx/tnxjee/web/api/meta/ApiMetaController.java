package org.truenewx.tnxjee.web.api.meta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.web.api.meta.model.ApiContext;
import org.truenewx.tnxjee.web.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.web.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.web.http.annotation.ResultFilter;
import org.truenewx.tnxjee.web.http.session.HeaderCookieSerializer;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * API元数据控制器
 */
@RestController
@RequestMapping("/api/meta")
@EnableConfigurationProperties(ApiMetaProperties.class)
public class ApiMetaController {
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private ApiModelMetaResolver metaResolver;
    @Autowired(required = false)
    private ApiMetaProperties properties;
    @Autowired(required = false)
    private HeaderCookieSerializer headerCookieSerializer;

    @GetMapping("/context")
    public ApiContext context(HttpServletRequest request) {
        ApiContext context = new ApiContext();
        if (this.properties != null) {
            context.setBaseUrl(this.properties.getBaseUrl());
            context.setContext(this.properties.getContext());
        }
        if (this.headerCookieSerializer != null) {
            String sessionId = request.getSession().getId();
            context.getHeaders().put(this.headerCookieSerializer.getHeaderName(), sessionId);
        }
        return context;
    }

    @GetMapping("/method")
    @SuppressWarnings("unchecked")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption" })
    public Map<String, ApiModelPropertyMeta> method(@RequestParam("url") String url,
            HttpServletRequest request) {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(url,
                HttpMethod.POST);
        if (handlerMethod != null) {
            for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
                if (methodParameter.getParameterAnnotation(RequestBody.class) != null) {
                    Class<?> parameterType = methodParameter.getParameterType();
                    if (Model.class.isAssignableFrom(parameterType)) {
                        Class<? extends Model> modelClass = (Class<? extends Model>) parameterType;
                        return this.metaResolver.resolve(modelClass, request.getLocale());
                    }
                }
            }
        }
        return null;
    }

}
