package org.truenewx.tnxjee.web.controller.http.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.truenewx.tnxjee.core.jackson.TypedPropertyFilter;
import org.truenewx.tnxjee.core.util.BeanUtil;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.web.controller.annotation.ResultFilter;
import org.truenewx.tnxjee.web.controller.servlet.mvc.method.HandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP消息JSON转换器
 */
public class JsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Autowired
    private HandlerMethodMapping handlerMethodMapping;

    private Map<String, ObjectMapper> mappers = new HashMap<>();

    public JsonHttpMessageConverter() {
        super(JsonUtil.copyDefaultMapper());
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
                                TypedPropertyFilter filter = new TypedPropertyFilter();
                                for (ResultFilter resultFilter : resultFilters) {
                                    Class<?> beanClass = resultFilter.type();
                                    if (beanClass == Object.class) {
                                        beanClass = object.getClass();
                                    }
                                    filter.addIncludedProperties(beanClass, resultFilter.included());
                                    filter.addExcludedProperties(beanClass, resultFilter.excluded());
                                }
                                mapper = JsonUtil.buildMapper(filter, filter.getTypes());
                                this.mappers.put(methodKey, mapper);
                            } else {
                                mapper = getObjectMapper();
                            }
                        }
                        String json = mapper.writeValueAsString(object);
                        System.out.println(json);
                    }
                } catch (Exception e) {
                    LogUtil.error(getClass(), e);
                }
            }
        }
        super.writeInternal(object, type, outputMessage);
    }

}
