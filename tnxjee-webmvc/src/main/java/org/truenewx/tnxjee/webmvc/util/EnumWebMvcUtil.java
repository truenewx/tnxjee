package org.truenewx.tnxjee.webmvc.util;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.SpringUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;

/**
 * 枚举类型在Web MVC场景中的工具类
 */
public class EnumWebMvcUtil {

    private EnumWebMvcUtil() {
    }

    /**
     * 抽取指定bean中的所有枚举类型字段为Map<br/>
     *
     * @param bean Bean对象
     * @return 枚举Map
     * @see org.truenewx.tnxjee.core.util.BeanUtil#extractEnumMap
     */
    public static Map<String, Object> extractEnumMap(Object bean) {
        HttpServletRequest request = SpringWebContext.getRequest();
        ApplicationContext context = SpringWebMvcUtil.getApplicationContext(request);
        EnumDictResolver enumDictResolver = SpringUtil.getFirstBeanByClass(context, EnumDictResolver.class);
        Locale locale = request == null ? null : request.getLocale();
        return BeanUtil.extractEnumMap(bean, enumDictResolver, locale);
    }

}
