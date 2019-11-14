package org.truenewx.tnxjee.web.controller.exception.message;

import java.util.Locale;

import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.service.api.exception.SingleException;

/**
 * Spring的业务异常消息解决器
 *
 * @author jianglei
 */
@Component
public class BusinessExceptionMessageResolver extends AbstractSingleExceptionMessageResolver {

    @Override
    public String resolveMessage(SingleException se, Locale locale) {
        if (se instanceof BusinessException) {
            BusinessException be = (BusinessException) se;
            if (be.isMessageLocalized()) {
                return be.getLocalizedMessage();
            }
            return this.messageSource.getMessage(be.getCode(), be.getArgs(), be.getCode(), locale);
        }
        return null;
    }

}
