package org.truenewx.tnxjee.web.feign;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.JsonUtil;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.MultiException;
import org.truenewx.tnxjee.service.exception.message.BusinessError;
import org.truenewx.tnxjee.web.exception.message.BusinessErrorBody;

import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * 业务错误解码器
 */
@Component
public class BusinessErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.status() == HttpStatus.FORBIDDEN.value()) {
                String json = IOUtils.toString(response.body().asReader());
                BusinessErrorBody body = JsonUtil.json2Bean(json, BusinessErrorBody.class);
                BusinessError[] errors = body.getErrors();
                if (errors.length == 1) {
                    return new BusinessException(errors[0]);
                } else if (errors.length > 1) {
                    BusinessException[] exceptions = new BusinessException[errors.length];
                    for (int i = 0; i < errors.length; i++) {
                        exceptions[i] = new BusinessException(errors[i]);
                    }
                    return new MultiException(exceptions);
                }
            }
        } catch (Exception e) {
            LogUtil.error(getClass(), e);
        }
        return super.decode(methodKey, response);
    }

}
