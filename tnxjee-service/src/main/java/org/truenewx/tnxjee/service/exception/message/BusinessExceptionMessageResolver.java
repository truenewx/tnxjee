package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.exception.BusinessException;

/**
 * 业务异常消息解决器
 *
 * @author jianglei
 */
@Component
public class BusinessExceptionMessageResolver implements SingleExceptionMessageResolver<BusinessException> {
    @Autowired
    private MessageSource messageSource;

    @Override
    public String resolveMessage(BusinessException be, Locale locale) {
        if (be.isMessageLocalized()) {
            return be.getLocalizedMessage();
        }
        return this.messageSource.getMessage(be.getCode(), be.getArgs(), be.getCode(), locale);
    }

}
