package org.truenewx.tnxjee.web.api.meta;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.web.api.meta.model.ApiContext;
import org.truenewx.tnxjee.web.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.web.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.web.http.annotation.ResultFilter;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

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

    @GetMapping("/context")
    public ApiContext context(HttpServletRequest request) {
        ApiContext context = new ApiContext();
        if (this.properties != null) {
            context.setBaseUrl(this.properties.getBaseUrl());
            context.setLoginSuccessRedirectParameter(
                    this.properties.getLoginSuccessRedirectParameter());
            context.setContext(this.properties.getContext());
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
