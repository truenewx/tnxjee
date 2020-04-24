package org.truenewx.tnxjee.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.web.bind.annotation.ResponseStream;
import org.truenewx.tnxjee.web.context.SpringWebContext;

/**
 * Spring Web工具类
 *
 * @author jianglei
 */
public class SpringWebUtil {

    private SpringWebUtil() {
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @param request HTTP请求
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext(HttpServletRequest request) {
        try {
            return RequestContextUtils.findWebApplicationContext(request);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    /**
     * 获取web项目应用范围内的ApplicationContext实例
     *
     * @return ApplicationContext实例
     */
    public static ApplicationContext getApplicationContext() {
        return getApplicationContext(SpringWebContext.getRequest());
    }

    /**
     * 先尝试从Spring的LocaleResolver中获取区域，以便以自定义的方式获取区域
     *
     * @param request 请求
     * @return 区域
     */
    public static Locale getLocale(HttpServletRequest request) {
        LocaleResolver localeResolver = SpringUtil
                .getFirstBeanByClass(getApplicationContext(request), LocaleResolver.class);
        if (localeResolver != null) {
            return localeResolver.resolveLocale(request);
        } else {
            return request.getLocale();
        }
    }

    public static boolean isResponseBody(HandlerMethod handlerMethod) {
        return handlerMethod.getMethodAnnotation(ResponseBody.class) != null
                || handlerMethod.getBeanType().getAnnotation(RestController.class) != null
                || handlerMethod.getMethodAnnotation(ResponseStream.class) != null;
    }

}
