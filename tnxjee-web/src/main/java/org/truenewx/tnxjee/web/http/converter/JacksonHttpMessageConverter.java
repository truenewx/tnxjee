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
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.enums.EnumDictResolver;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.web.http.annotation.ResultFilter;
import org.truenewx.tnxjee.web.servlet.mvc.method.HandlerMethodMapping;

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

    private Map<String, ObjectMapper> mappers = new HashMap<>();

    public JacksonHttpMessageConverter() {
        super(JsonUtil.copyDefaultMapper());
        setDefaultCharset(StandardCharsets.UTF_8);
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        if (outputMessage instanceof ServletServerHttpResponse) {
            ServletServerHttpResponse response = (ServletServerHttpResponse) outputMessage;
            HttpServletRequest request = BeanUtil.getFieldValue(response.getServletResponse(), HttpServletRequest.class);
            if (request != null) {
                try {
                    HandlerMethod handlerMethod = this.handlerMethodMapping.getHandlerMethod(request);
                    if (handlerMethod != null) {
                        Method method = handlerMethod.getMethod();
                        String methodKey = method.toString();
                        ObjectMapper mapper = this.mappers.get(methodKey);
                        if (mapper == null) {
                            ResultFilter[] resultFilters = method.getAnnotationsByType(ResultFilter.class);
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
                                this.mappers.put(methodKey, mapper);
                            } else { // 没有结果过滤设置的取默认映射器
                                mapper = getObjectMapper();
                            }
                        }
                        String json = mapper.writeValueAsString(object);
                        IOUtils.write(json, response.getBody(), getDefaultCharset());
                        return;
                    }
                } catch (Exception e) {
                    LogUtil.error(getClass(), e);
                }
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

    protected EnumTypePropertyFilter createPropertyFilter() {
        return new EnumTypePropertyFilter();
    }

}
