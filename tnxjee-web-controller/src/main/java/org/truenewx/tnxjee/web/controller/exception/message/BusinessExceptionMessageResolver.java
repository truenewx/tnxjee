package org.truenewx.tnxjee.web.controller.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.enums.support.EnumDictResolver;
import org.truenewx.tnxjee.model.exception.BusinessException;
import org.truenewx.tnxjee.model.exception.SingleException;

/**
 * Spring的业务异常消息解决器
 *
 * @author jianglei
 */
@Component
public class BusinessExceptionMessageResolver extends AbstractSingleExceptionMessageResolver {

    private EnumDictResolver enumDictResolver;

    @Autowired(required = false)
    public void setEnumDictResolver(EnumDictResolver enumDictResolver) {
        this.enumDictResolver = enumDictResolver;
    }

    @Override
    public String resolveMessage(SingleException se, Locale locale) {
        if (se instanceof BusinessException) {
            BusinessException be = (BusinessException) se;
            Object[] args = be.getArgs();
            if (this.enumDictResolver != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Enum) {
                        args[i] = this.enumDictResolver.getText((Enum<?>) args[i], locale);
                    }
                }
            }
            return this.messageSource.getMessage(be.getCode(), args, be.getCode(), locale);
        }
        return null;
    }

}
