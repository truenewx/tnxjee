package org.truenewx.tnxjee.webmvc.api.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.config.AppConfiguration;
import org.truenewx.tnxjee.core.config.AppConstants;
import org.truenewx.tnxjee.core.config.CommonProperties;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.core.enums.EnumType;
import org.truenewx.tnxjee.core.util.CollectionUtil;
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
    private EnumDictResolver enumDictResolver;
    @Autowired
    private ApiMetaProperties apiMetaProperties;
    @Autowired
    private CommonProperties commonProperties;
    @Value(AppConstants.EL_SPRING_APP_NAME)
    private String baseApp;

    @GetMapping("/context")
    public ApiContext context() {
        ApiContext context = new ApiContext();
        context.setBaseApp(this.baseApp);
        context.setLoginSuccessRedirectParameter(this.apiMetaProperties.getLoginSuccessRedirectParameter());
        Set<String> appNames = CollectionUtil.toSet(this.apiMetaProperties.getAppNames());
        if (CollectionUtils.isEmpty(appNames)) {
            context.getApps().putAll(this.commonProperties.getAppContextUriMapping());
        } else {
            appNames.add(this.baseApp); // 至少包含当前应用
            for (String appName : appNames) {
                AppConfiguration app = this.commonProperties.getApp(appName);
                if (app != null) {
                    context.getApps().put(appName, app.getContextUri(false));
                }
            }
        }
        return context;
    }

    @GetMapping("/method")
    @SuppressWarnings("unchecked")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption" })
    @ResultFilter(type = ApiModelPropertyMeta.class, pureEnum = "type")
    public Map<String, ApiModelPropertyMeta> method(@RequestParam("url") String url, HttpServletRequest request) {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(url, HttpMethod.POST);
        if (handlerMethod != null) {
            for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
                if (methodParameter.getParameterAnnotation(RequestBody.class) != null) {
                    Class<?> parameterType = methodParameter.getParameterType();
                    if (Model.class.isAssignableFrom(parameterType)) {
                        Class<? extends Model> modelClass = (Class<? extends Model>) parameterType;
                        Map<String, ApiModelPropertyMeta> metas = this.metaResolver
                                .resolve(modelClass, request.getLocale());
                        return metas; // 为了便于调试，抽取变量
                    }
                }
            }
        }
        return null;
    }

    @GetMapping("/enums")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption" })
    public Collection<EnumItem> enumItems(@RequestParam("type") String type,
            @RequestParam(value = "subtype", required = false) String subtype, HttpServletRequest request) {
        EnumType enumType = this.enumDictResolver.getEnumType(type, subtype, request.getLocale());
        if (enumType != null) {
            return enumType.getItems();
        }
        return Collections.emptyList();
    }

}
