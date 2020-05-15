package org.truenewx.tnxjee.core.jackson;

import java.util.Map;
import java.util.function.Predicate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 具有附加判断的类型解决器构建器
 */
public class PredicateTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    private static final long serialVersionUID = -6850144227414223439L;

    private Predicate<Class<?>> predicate = clazz -> true;

    public PredicateTypeResolverBuilder(ObjectMapper.DefaultTyping t) {
        super(t);
    }

    public static PredicateTypeResolverBuilder NON_CONCRETE_AND_COLLECTION = createDefault(clazz -> {
        return !Iterable.class.isAssignableFrom(clazz) && !Map.class.isAssignableFrom(clazz);
    });

    public static PredicateTypeResolverBuilder createDefault(Predicate<Class<?>> predicate) {
        PredicateTypeResolverBuilder builder = new PredicateTypeResolverBuilder(
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        builder.init(JsonTypeInfo.Id.CLASS, null)
                .inclusion(JsonTypeInfo.As.PROPERTY)
                .typeProperty(JsonTypeInfo.Id.CLASS.getDefaultPropertyName());
        return builder.predicate(predicate);
    }

    public PredicateTypeResolverBuilder predicate(Predicate<Class<?>> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public boolean useForType(JavaType t) {
        return super.useForType(t) && this.predicate.test(t.getRawClass());
    }

}
