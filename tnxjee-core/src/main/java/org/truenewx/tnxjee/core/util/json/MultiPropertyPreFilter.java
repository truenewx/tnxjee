package org.truenewx.tnxjee.core.util.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * 多类型属性过滤器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class MultiPropertyPreFilter implements PropertyPreFilter {

    private Map<Class<?>, SimplePropertyPreFilter> filters = new HashMap<>();

    public MultiPropertyPreFilter() {
    }

    public MultiPropertyPreFilter(Class<?> type, Set<String> includedProperties,
            Set<String> excludedProperties) {
        addFilteredProperties(type, includedProperties, excludedProperties);
    }

    public MultiPropertyPreFilter(Class<?> type, String[] includedProperties,
            String[] excludedProperties) {
        addFilteredProperties(type, includedProperties, excludedProperties);
    }

    public void addFilteredProperties(Class<?> type, Set<String> includedProperties,
            Set<String> excludedProperties) {
        SimplePropertyPreFilter filter = getFilter(type);
        if (includedProperties != null) {
            filter.getIncludes().addAll(includedProperties);
        }
        if (excludedProperties != null) {
            filter.getExcludes().addAll(excludedProperties);
        }
    }

    private SimplePropertyPreFilter getFilter(Class<?> type) {
        SimplePropertyPreFilter filter = this.filters.get(type);
        if (filter == null) {
            filter = new SimplePropertyPreFilter(type);
            this.filters.put(type, filter);
        }
        return filter;
    }

    public void addFilteredProperties(Class<?> type, String[] includedProperties,
            String[] excludedProperties) {
        SimplePropertyPreFilter filter = getFilter(type);
        if (includedProperties != null) {
            Set<String> includes = filter.getIncludes();
            for (String property : includedProperties) {
                includes.add(property);
            }
        }
        if (excludedProperties != null) {
            Set<String> excludes = filter.getExcludes();
            for (String property : excludedProperties) {
                excludes.add(property);
            }
        }
    }

    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        if (object == null) {
            return true;
        }
        PropertyPreFilter filter = null;
        Class<? extends Object> clazz = object.getClass();
        if (clazz != Object.class) {
            filter = this.filters.get(clazz);
            while (filter == null) {
                clazz = clazz.getSuperclass();
                if (clazz == Object.class) {
                    break;
                }
                filter = this.filters.get(clazz);
            }
        }
        if (filter != null) {
            return filter.apply(serializer, object, name);
        }
        return true;
    }

}
