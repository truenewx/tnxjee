package org.truenewx.tnxjee.web.servlet.mvc.method;

import org.springframework.web.util.HtmlUtils;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.annotation.Escaped;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Bean属性转义处理器
 */
public class BeanPropertyEscapeProcessor implements BiConsumer<Object, Boolean> {

    public static final BeanPropertyEscapeProcessor INSTANCE = new BeanPropertyEscapeProcessor();

    private Map<Class<?>, Set<String>> beanPropertiesMapping = new HashMap<>();

    private BeanPropertyEscapeProcessor() {
    }

    @Override
    public void accept(Object object, Boolean opposite) {
        if (object != null) {
            Class<?> clazz = object.getClass();
            Set<String> properties = this.beanPropertiesMapping.get(clazz);
            if (properties == null) {
                Set<String> escapedProperties = new HashSet<>();
                ClassUtil.loopFields(clazz, Escaped.class, ((field, escaped) -> {
                    // 字符串类型且需HTML转义的字段才是有效的转义字段
                    Class<?> type = field.getType();
                    if (type == String.class) {
                        escapedProperties.add(field.getName());
                    } else if (ClassUtil.isComplex(type)) { // 复合类型递归转义处理
                        accept(BeanUtil.getPropertyValue(object, field.getName()), opposite);
                    }
                    return true;
                }));
                properties = escapedProperties;
                this.beanPropertiesMapping.put(clazz, properties);
            }
            for (String propertyName : properties) {
                Object value = BeanUtil.getPropertyValue(object, propertyName);
                if (value instanceof String) {
                    String escapedValue = opposite ? HtmlUtils.htmlUnescape((String) value)
                            : HtmlUtils.htmlEscape((String) value);
                    if (!value.equals(escapedValue)) {
                        BeanUtil.setPropertyValue(object, propertyName, escapedValue);
                    }
                }
            }
        }
    }

}
