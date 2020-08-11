package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.i18n.PropertyCaptionResolver;
import org.truenewx.tnxjee.service.exception.BusinessException;
import org.truenewx.tnxjee.service.exception.FormatException;
import org.truenewx.tnxjee.service.exception.SingleException;

/**
 * 业务异常消息解决器
 *
 * @author jianglei
 */
@Component
public class SingleExceptionMessageResolverImpl implements SingleExceptionMessageResolver {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PropertyCaptionResolver propertyCaptionResolver;

    @Override
    public String resolveMessage(SingleException se, Locale locale) {
        if (se.isMessageLocalized()) {
            return se.getLocalizedMessage();
        }
        if (se instanceof BusinessException) {
            BusinessException be = (BusinessException) se;
            return this.messageSource.getMessage(be.getCode(), be.getArgs(), be.getCode(), locale);
        } else if (se instanceof FormatException) {
            FormatException fe = (FormatException) se;
            String propertyCaption = this.propertyCaptionResolver
                    .resolveCaption(fe.getModelClass(), fe.getProperty(), locale);
            if (propertyCaption == null) {
                propertyCaption = fe.getProperty(); // 如果均未取到，则取属性名
            }
            String message =
                    this.messageSource.getMessage(fe.getCode(), null, fe.getCode(), locale);
            return propertyCaption + message;
        }
        return null;
    }

}
