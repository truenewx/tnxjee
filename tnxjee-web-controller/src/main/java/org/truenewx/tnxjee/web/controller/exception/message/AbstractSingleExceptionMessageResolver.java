package org.truenewx.tnxjee.web.controller.exception.message;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * 基于Spring的抽象单异常消息解决器
 *
 * @author jianglei
 */
public abstract class AbstractSingleExceptionMessageResolver
        implements SingleExceptionMessageResolver, MessageSourceAware {

    protected MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
