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
import org.springframework.beans.factory.InitializingBean;
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
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.webmvc.http.annotation.ResultFilter;
import org.truenewx.tnxjee.webmvc.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.webmvc.util.RpcUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * 基于Jackson实现的HTTP消息JSON转换器
 */
@Component
public class JacksonHttpMessageConverter extends MappingJackson2HttpMessageConverter implements InitializingBean {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;
    @Autowired
    private EnumDictResolver enumDictResolver;

    private final Map<String, ObjectMapper> mappers = new HashMap<>();

    public JacksonHttpMessageConverter() {
        super(JsonUtil.copyDefaultMapper());
        setDefaultCharset(StandardCharsets.UTF_8);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 默认JSON映射器具有枚举常量附加显示名称字段的能力
        withSerializerModifier(getObjectMapper(), new BeanEnumSerializerModifier(this.enumDictResolver));
    }

    private void withSerializerModifier(ObjectMapper mapper, BeanSerializerModifier modifier) {
        mapper.setSerializerFactory(mapper.getSerializerFactory().withSerializerModifier(modifier));
    }

    private String getMapperKey(boolean internal, Method method) {
        return (internal ? "in:" : "ex:") + method.toString();
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            try {
                HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
                if (handlerMethod != null) {
                    Method method = handlerMethod.getMethod();
                    boolean internal = RpcUtil.isInternalRpc(request);
                    ObjectMapper mapper = getMapper(internal, method, object);
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

    private ObjectMapper getMapper(boolean internal, Method method, Object object) {
        String mapperKey = getMapperKey(internal, method);
        ObjectMapper mapper = this.mappers.get(mapperKey);
        if (mapper == null) {
            Class<?> resultType = object.getClass();
            ResultFilter[] resultFilters = method.getAnnotationsByType(ResultFilter.class);
            if (ArrayUtils.isNotEmpty(resultFilters)) {
                mapper = buildMapper(internal, resultType, resultFilters);
                this.mappers.put(mapperKey, mapper);
            } else { // 没有结果过滤设置的取默认映射器
                mapper = getObjectMapper();
            }
        }
        return mapper;
    }

    @SuppressWarnings("deprecation")
    private ObjectMapper buildMapper(boolean internal, Class<?> resultType, ResultFilter[] resultFilters) {
        TypedPropertyFilter filter = new TypedPropertyFilter();
        BeanEnumSerializerModifier modifier = new BeanEnumSerializerModifier(this.enumDictResolver);
        Collection<Class<?>> withClassFieldTypes = new ArrayList<>();
        for (ResultFilter resultFilter : resultFilters) {
            Class<?> filteredType = resultFilter.type();
            if (filteredType == Object.class) {
                filteredType = resultType;
            }
            filter.addIncludedProperties(filteredType, resultFilter.included());
            filter.addExcludedProperties(filteredType, resultFilter.excluded());
            modifier.addIgnoredPropertiesNames(filteredType, resultFilter.pureEnum());
            if (resultFilter.withClassField()) {
                withClassFieldTypes.add(filteredType);
            }
        }
        // 被过滤的类型中如果不包含结果类型，则加入结果类型，以确保至少包含结果类型
        Class<?>[] filteredTypes = filter.getTypes();
        if (!ArrayUtils.contains(filteredTypes, resultType)) {
            filteredTypes = ArrayUtils.add(filteredTypes, resultType);
        }
        ObjectMapper mapper = JsonUtil.buildMapper(filter, filteredTypes);
        withSerializerModifier(mapper, modifier);
        // 内部请求，且含有需要附带类型字段的类型时，激活附带类型字段的功能
        if (internal && withClassFieldTypes.size() > 0) {
            mapper.setDefaultTyping(PredicateTypeResolverBuilder.createNonConcrete(clazz -> {
                for (Class<?> filterType : withClassFieldTypes) {
                    if (filterType.isAssignableFrom(clazz)) {
                        return true;
                    }
                }
                return false;
            }));
        }
        return mapper;
    }

}
