package org.truenewx.tnxjee.webmvc.http.converter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.jackson.BeanEnumSerializerModifier;
import org.truenewx.tnxjee.core.jackson.PredicateTypeResolverBuilder;
import org.truenewx.tnxjee.core.jackson.TypedPropertyFilter;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.webmvc.util.RpcUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 基于Jackson实现的HTTP消息JSON转换器
 */
@Component
public class JacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private EnumDictResolver enumDictResolver;

    private final ObjectMapper defaultMapper; // 不生成@class字段的默认映射器
    private final Map<String, ObjectMapper> externalMappers = new HashMap<>(); // 对外的JSON映射器集，不含@class
    private final Map<String, ObjectMapper> internalMappers = new HashMap<>(); // 对内的JSON映射器集，包含@class

    public JacksonHttpMessageConverter() {
        // 默认JSON映射器包含@class构建，以便于在读取时可反序列化包含@class的JSON串
        super(JsonUtil.copyClassedMapper());
        setDefaultCharset(StandardCharsets.UTF_8);
        this.defaultMapper = JsonUtil.copyDefaultMapper();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            boolean internal = RpcUtil.isInternalRpc(request);
            try {
                HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
                if (handlerMethod != null) {
                    Method method = handlerMethod.getMethod();
                    String methodKey = method.toString();
                    ObjectMapper mapper = internal ? this.internalMappers.get(methodKey)
                            : this.externalMappers.get(methodKey);
                    if (mapper == null) {
                        Class<?> resultType = object.getClass();
                        ResultFilter[] resultFilters = method.getAnnotationsByType(ResultFilter.class);
                        if (ArrayUtils.isNotEmpty(resultFilters) || ClassUtil.hasReadableEnumProperty(resultType)) {
                            TypedPropertyFilter filter = new TypedPropertyFilter();
                            BeanEnumSerializerModifier modifier = new BeanEnumSerializerModifier(this.enumDictResolver);
                            Collection<Class<?>> withoutClassFieldTypes = new ArrayList<>();
                            for (ResultFilter resultFilter : resultFilters) {
                                Class<?> filteredType = resultFilter.type();
                                if (filteredType == Object.class) {
                                    filteredType = resultType;
                                }
                                filter.addIncludedProperties(filteredType, resultFilter.included());
                                filter.addExcludedProperties(filteredType, resultFilter.excluded());
                                modifier.addIgnoredPropertiesNames(filteredType, resultFilter.pureEnum());
                                if (!resultFilter.withClass()) {
                                    withoutClassFieldTypes.add(filteredType);
                                }
                            }
                            // 被过滤的类型中如果不包含结果类型，则加入结果类型，以确保至少包含结果类型
                            Class<?>[] filteredTypes = filter.getTypes();
                            if (!ArrayUtils.contains(filteredTypes, resultType)) {
                                filteredTypes = ArrayUtils.add(filteredTypes, resultType);
                            }
                            mapper = JsonUtil.buildMapper(filter, filteredTypes);
                            // 注册枚举常量序列化器
                            mapper.setSerializerFactory(mapper.getSerializerFactory().withSerializerModifier(modifier));
                            if (internal) {
                                mapper.setDefaultTyping(PredicateTypeResolverBuilder.createNonConcrete(clazz -> {
                                    for (Class<?> filterType : withoutClassFieldTypes) {
                                        if (filterType.isAssignableFrom(clazz)) {
                                            return false;
                                        }
                                    }
                                    return PredicateTypeResolverBuilder.PREDICATE_NON_COLLECTION.test(clazz);
                                }));
                                this.internalMappers.put(methodKey, mapper);
                            } else {
                                this.externalMappers.put(methodKey, mapper);
                            }
                        } else { // 没有结果过滤设置的取默认映射器
                            mapper = internal ? getObjectMapper() : this.defaultMapper;
                        }
                    }
                    String json = mapper.writeValueAsString(object);
                    Charset charset = Objects.requireNonNullElse(getDefaultCharset(), StandardCharsets.UTF_8);
                    IOUtils.write(json, outputMessage.getBody(), charset.name());
                    return;
                }
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

}
