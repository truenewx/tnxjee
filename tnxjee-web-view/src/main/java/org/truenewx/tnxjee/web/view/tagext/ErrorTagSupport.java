package org.truenewx.tnxjee.web.view.tagext;

import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.service.api.exception.BusinessException;
import org.truenewx.tnxjee.service.api.exception.ResolvableException;
import org.truenewx.tnxjee.service.api.exception.MultiException;
import org.truenewx.tnxjee.service.api.exception.SingleException;
import org.truenewx.tnxjee.web.controller.exception.resolver.ResolvedBusinessError;
import org.truenewx.tnxjee.web.view.exception.resolver.ViewBusinessExceptionResolver;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlTagSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 错误标签支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ErrorTagSupport extends ThymeleafHtmlTagSupport {

    protected List<ResolvedBusinessError> getErrors(ThymeleafElementTagContext context) {
        return context.getRequestAttributeValue(ViewBusinessExceptionResolver.ATTRIBUTE_ERRORS);
    }

}
