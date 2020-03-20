package org.truenewx.tnxjee.core.caption;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * 显示名称工具类
 *
 * @author jianglei
 * 
 */
public class CaptionUtil {

    private CaptionUtil() {
    }

    private static String getCaptionValue(Caption[] captionAnnotations, Locale locale) {
        Caption caption = null;
        for (Caption captionAnnotation : captionAnnotations) {
            if (StringUtils.isBlank(captionAnnotation.locale())) {
                // 暂存默认语言的Caption注解
                caption = captionAnnotation;
            } else if (locale.toString().equals(captionAnnotation.locale())) {
                // 找到语言匹配的Caption注解
                caption = captionAnnotation;
                break;
            }
        }
        return caption == null ? null : caption.value();
    }

    public static String getCaption(Collection<Annotation> annotations, Locale locale) {
        Caption caption = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof Caption) {
                Caption captionAnnotation = (Caption) annotation;
                if (StringUtils.isBlank(captionAnnotation.locale())) {
                    // 暂存默认语言的Caption注解
                    caption = captionAnnotation;
                } else if (locale.toString().equals(captionAnnotation.locale())) {
                    // 找到语言匹配的Caption注解
                    caption = captionAnnotation;
                    break;
                }
            }
        }
        return caption == null ? null : caption.value();
    }

    public static String getCaption(Class<?> clazz, Locale locale) {
        Caption[] captionAnnonations = clazz.getAnnotationsByType(Caption.class);
        return getCaptionValue(captionAnnonations, locale);
    }

    public static String getCaption(Method method, Locale locale) {
        Caption[] captionAnnonations = method.getAnnotationsByType(Caption.class);
        return getCaptionValue(captionAnnonations, locale);
    }

    public static String getCaption(Parameter parameter, Locale locale) {
        Caption[] captionAnnonations = parameter.getAnnotationsByType(Caption.class);
        return getCaptionValue(captionAnnonations, locale);
    }

    public static String getCaption(Field field, Locale locale) {
        Caption[] captionAnnonations = field.getAnnotationsByType(Caption.class);
        return getCaptionValue(captionAnnonations, locale);
    }

}
