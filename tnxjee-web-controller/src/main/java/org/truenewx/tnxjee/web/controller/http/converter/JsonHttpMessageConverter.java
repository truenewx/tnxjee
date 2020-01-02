package org.truenewx.tnxjee.web.controller.http.converter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

/**
 * Json的HTTP消息转换器
 */
public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    public JsonHttpMessageConverter() {
        super();
        FastJsonConfig fastJsonConfig = getFastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat,
                SerializerFeature.DisableCircularReferenceDetect);
        // TODO 添加SerializeFilter以支持RPC返回结果定制化
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpHeaders headers = outputMessage.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        super.writeInternal(object, outputMessage);
    }

}
