package org.truenewx.tnxjee.web.api.meta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.enums.EnumItem;
import org.truenewx.tnxjee.model.Model;
import org.truenewx.tnxjee.web.http.annotation.ResultFilter;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    @GetMapping
    @SuppressWarnings("unchecked")
    @ResultFilter(type = EnumItem.class, included = { "key", "caption" })
    public Map<String, Object> get(@RequestParam("url") String url,
            HttpServletRequest request) {
        HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(url,
                HttpMethod.POST);
        if (handlerMethod != null) {
            for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
                if (methodParameter.getParameterAnnotation(RequestBody.class) != null) {
                    Class<?> parameterType = methodParameter.getParameterType();
                    if (Model.class.isAssignableFrom(parameterType)) {
                        Class<? extends Model> modelClass = (Class<? extends Model>) parameterType;
                        ApiModelMeta meta = this.metaResolver.resolve(modelClass, request.getLocale());
                        return meta.asMap();
                    }
                }
            }
        }
        return null;
    }

}
