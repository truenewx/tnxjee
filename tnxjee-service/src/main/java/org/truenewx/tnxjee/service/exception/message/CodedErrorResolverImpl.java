package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.service.exception.model.CodedError;

@Component
public class CodedErrorResolverImpl implements CodedErrorResolver {

    @Autowired
    private SingleExceptionMessageResolver messageResolver;

    @Override
    public CodedError resolveError(String code, Locale locale, Object... args) {
        String message = this.messageResolver.resolveMessage(code, locale, args);
        return new CodedError(code, message);
    }

}
