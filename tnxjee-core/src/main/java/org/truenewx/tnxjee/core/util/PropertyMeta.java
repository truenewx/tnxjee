package org.truenewx.tnxjee.core.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 属性元数据
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class PropertyMeta {
    private String name;
    private Class<?> type;
    private List<Annotation> annotations = new ArrayList<>();

    public PropertyMeta(final String name, final Class<?> type, final Annotation... annotations) {
        this.name = name;
        this.type = type;
        addAnnotations(annotations);
    }

    public void addAnnotations(final Annotation[] annotations) {
        if (annotations != null) {
            for (final Annotation annotation : annotations) {
                if (annotation != null) {
                    this.annotations.add(annotation);
                }
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public List<Annotation> getAnnotations() {
        return this.annotations;
    }
}
