package org.truenewx.tnxjee.core.jackson;

import org.truenewx.tnxjee.core.util.JacksonUtil;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * 非具态类型解决器构建器
 */
public class NonConcreteTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    private static final long serialVersionUID = -1000737704428050672L;

    public NonConcreteTypeResolverBuilder() {
        super(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, LaissezFaireSubTypeValidator.instance);
    }

    @Override
    public boolean useForType(JavaType type) {
        if (type.isPrimitive() || type.getContentType() != null) {
            return false;
        }
        Class<?> clazz = type.getRawClass();
        if (TreeNode.class.isAssignableFrom(clazz)) {
            return false;
        }
        return type.isJavaLangObject() || JacksonUtil.isSerializableNonConcrete(clazz);
    }

}
