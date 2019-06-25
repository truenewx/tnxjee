package org.truenewx.tnxjee.core.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.BeforeFilter;

/**
 * 附加类型字段的序列化过滤器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TypeSerializeFilter extends BeforeFilter {

    private Class<?>[] baseClasses;

    public TypeSerializeFilter(Class<?>... baseClasses) {
        this.baseClasses = baseClasses;
    }

    private boolean isAppendable(Class<?> type) {
        for (Class<?> baseClass : this.baseClasses) {
            if (baseClass.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeBefore(Object object) {
        if (object != null) {
            Class<?> type = object.getClass();
            if (isAppendable(type)) {
                writeKeyValue(JSON.DEFAULT_TYPE_KEY, type.getName());
            }
        }
    }

}
