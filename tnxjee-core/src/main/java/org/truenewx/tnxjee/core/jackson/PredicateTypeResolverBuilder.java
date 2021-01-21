package org.truenewx.tnxjee.core.jackson;

import java.util.function.Predicate;

import org.truenewx.tnxjee.core.util.ClassUtil;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * 具有附加判断的类型解决器构建器
 */
public class PredicateTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    private static final long serialVersionUID = -1000737704428050672L;

    private Predicate<Class<?>> predicate;

    public PredicateTypeResolverBuilder(ObjectMapper.DefaultTyping t) {
        super(t, LaissezFaireSubTypeValidator.instance);
    }

    public static final Predicate<Class<?>> PREDICATE_NON_COLLECTION = clazz -> {
        return !clazz.isArray() && ClassUtil.isComplex(clazz);
    };

    public static PredicateTypeResolverBuilder NON_CONCRETE_AND_COLLECTION = createNonConcrete(
            PREDICATE_NON_COLLECTION);

    public static PredicateTypeResolverBuilder createNonConcrete(Predicate<Class<?>> predicate) {
        PredicateTypeResolverBuilder builder = new PredicateTypeResolverBuilder(
                ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);
        builder.init(JsonTypeInfo.Id.CLASS, null).inclusion(JsonTypeInfo.As.PROPERTY)
                .typeProperty(JsonTypeInfo.Id.CLASS.getDefaultPropertyName());
        return builder.predicate(predicate);
    }

    public PredicateTypeResolverBuilder predicate(Predicate<Class<?>> predicate) {
        this.predicate = predicate;
        return this;
    }

    @Override
    public boolean useForType(JavaType t) {
        if (this.predicate != null) {
            return this.predicate.test(t.getRawClass());
        }
        return super.useForType(t);
    }

}
