package org.truenewx.tnxjee.webmvc.api.meta;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiContext;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiMetaProperties;
import org.truenewx.tnxjee.webmvc.api.meta.model.ApiModelPropertyMeta;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;

/**
 * API元数据控制器
 */
@RestController
@RequestMapping("/api/meta")
public class ApiMetaController {
    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private ApiModelMetaResolver metaResolver;
    @Autowired
    private ApiMetaProperties apiMetaProperties;
    @Autowired
    private CommonProperties commonProperties;

    @GetMapping("/context")
    public ApiContext context(HttpServletRequest request) {
        ApiContext context = new ApiContext();
        String baseApp = this.apiMetaProperties.getBaseApp();
        if (StringUtils.isNotBlank(baseApp)) {
            AppConfiguration app = this.commonProperties.getApp(baseApp);
            if (app != null) {
                context.setBaseUrl(app.getContextUri());
            }
        }
        context.setLoginSuccessRedirectParameter(
                this.apiMetaProperties.getLoginSuccessRedirectParameter());
        String[] appNames = this.apiMetaProperties.getAppNames();
        if (ArrayUtils.isEmpty(appNames)) {
            context.getApps().putAll(this.commonProperties.getAppContextUris());
        } else {
            for (String appName : appNames) {
                AppConfiguration app = this.commonProperties.getApp(appName);
                if (app != null) {
                    context.getApps().put(appName, app.getContextUri());
                }
            }
            AppConfiguration selfApp = this.commonProperties.getSelfApp();
            if (selfApp != null) {
                context.getApps().put("self", selfApp.getContextUri());
            }
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
