package org.truenewx.tnxjee.service.api.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.i18n.PropertyCaptionResolver;
import org.truenewx.tnxjee.service.api.exception.FormatException;

/**
 * 格式异常消息解决器
 *
 * @author jianglei
 */
@Component
public class FormatExceptionMessageResolver implements SingleExceptionMessageResolver<FormatException> {

    @Autowired
    private PropertyCaptionResolver propertyCaptionResolver;

    @Override
    public String resolveMessage(FormatException fe, Locale locale) {
        String propertyCaption = this.propertyCaptionResolver.resolveCaption(fe.getBeanClass(),
                fe.getProperty(), locale);
        if (propertyCaption == null) {
            propertyCaption = fe.getProperty(); // 如果均未取到，则取属性名
        }
        return propertyCaption + fe.getMessage();
    }

}
