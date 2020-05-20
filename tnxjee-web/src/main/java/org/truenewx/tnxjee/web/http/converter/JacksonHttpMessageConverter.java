package org.truenewx.tnxjee.web.http.converter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
import org.truenewx.tnxjee.core.jackson.PredicateTypeResolverBuilder;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.web.context.SpringWebContext;
import org.truenewx.tnxjee.web.http.annotation.ResultFilter;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;
import org.truenewx.tnxjee.web.util.WebConstants;

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

    private final ObjectMapper defaultExternalMapper; // 默认的对外JSON映射器，不含@class
    private final Map<String, ObjectMapper> externalMappers = new HashMap<>(); // 对外的JSON映射器集，不含@class
    private final Map<String, ObjectMapper> internalMappers = new HashMap<>(); // 对内的JSON映射器集，包含@class

    public JacksonHttpMessageConverter() {
        // 默认JSON映射器包含@class构建，以便于在读取时可反序列化包含@class的JSON串
        super(JsonUtil.copyNonConcreteAndCollectionMapper());
        setDefaultCharset(StandardCharsets.UTF_8);
        this.defaultExternalMapper = JsonUtil.copyDefaultMapper();
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpServletRequest request = SpringWebContext.getRequest();
        if (request != null) {
            boolean internal = isInternalRpc(request);
            try {
                HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
                if (handlerMethod != null) {
                    Method method = handlerMethod.getMethod();
                    String methodKey = method.toString();
                    ObjectMapper mapper = internal ? this.internalMappers.get(methodKey)
                            : this.externalMappers.get(methodKey);
                    if (mapper == null) {
                        ResultFilter[] resultFilters = method
                                .getAnnotationsByType(ResultFilter.class);
                        if (ArrayUtils.isNotEmpty(resultFilters)) {
                            EnumTypePropertyFilter filter = createPropertyFilter();
                            filter.setEnumDictResolver(this.enumDictResolver);
                            for (ResultFilter resultFilter : resultFilters) {
                                Class<?> beanClass = resultFilter.type();
                                if (beanClass == Object.class) {
                                    beanClass = object.getClass();
                                }
                                filter.addIncludedProperties(beanClass, resultFilter.included());
                                filter.addExcludedProperties(beanClass, resultFilter.excluded());
                                filter.addPureEnumProperties(beanClass, resultFilter.pureEnum());
                            }
                            mapper = JsonUtil.buildMapper(filter, filter.getTypes());
                            if (internal) {
                                mapper.setDefaultTyping(PredicateTypeResolverBuilder.NON_CONCRETE_AND_COLLECTION);
                                this.internalMappers.put(methodKey, mapper);
                            } else {
                                this.externalMappers.put(methodKey, mapper);
                            }
                        } else { // 没有结果过滤设置的取默认映射器
                            mapper = internal ? getObjectMapper() : this.defaultExternalMapper;
                        }
                    }
                    String json = mapper.writeValueAsString(object);
                    IOUtils.write(json, outputMessage.getBody(), getDefaultCharset());
                    return;
                }
            } catch (Exception e) {
                LogUtil.error(getClass(), e);
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

    private boolean isInternalRpc(HttpServletRequest request) {
        String internalRpc = request.getHeader(WebConstants.HEADER_INTERNAL_RPC);
        if (internalRpc != null) {
            return Boolean.parseBoolean(internalRpc);
        }
        String userAgent = request.getHeader("User-Agent");
        return userAgent == null || userAgent.toLowerCase().startsWith("java");
    }

    protected EnumTypePropertyFilter createPropertyFilter() {
        return new EnumTypePropertyFilter();
    }

}
