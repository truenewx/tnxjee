package org.truenewx.tnxjee.web.controller.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.i18n.PropertyCaptionResolver;
import org.truenewx.tnxjee.model.exception.FormatException;
import org.truenewx.tnxjee.model.exception.SingleException;

/**
 * 格式异常消息解决器
 *
 * @author jianglei
 */
@Component
public class FormatExceptionMessageResolver extends AbstractSingleExceptionMessageResolver {

    @Autowired
    private PropertyCaptionResolver propertyCaptionResolver;

    @Override
    public String resolveMessage(SingleException se, Locale locale) {
        if (se instanceof FormatException) {
            FormatException fe = (FormatException) se;
            String propertyCaption = this.propertyCaptionResolver.resolveCaption(fe.getBeanClass(),
                    fe.getProperty(), locale);
            if (propertyCaption == null) {
                propertyCaption = fe.getProperty(); // 如果均未取到，则取属性名
            }
            return propertyCaption + fe.getMessage();
        }
        return null;
    }

}
