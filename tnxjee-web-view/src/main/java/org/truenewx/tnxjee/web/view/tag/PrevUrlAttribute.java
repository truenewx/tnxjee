package org.truenewx.tnxjee.web.view.tag;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.web.controller.spring.context.SpringWebContext;
import org.truenewx.tnxjee.web.controller.spring.servlet.mvc.Loginer;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlAttributeSupport;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlTagSupport;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 输出前一个请求URL的属性
 *
 * @author jianglei
 */
@Component
public class PrevUrlAttribute extends ThymeleafHtmlAttributeSupport {

    @Override
    protected String getAttributeName() {
        return "prev-url";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        HttpServletRequest request = SpringWebContext.getRequest();
        String prevUrl = WebViewUtil.getPrevUrl(request);
        String href;
        if (prevUrl != null) {
            href = prevUrl;
        } else {
            href = context.getAttributeValue("href");
        }
        if (href != null) {
            String contextPath = request.getContextPath();
            if (!contextPath.equals(Strings.SLASH) && !href.startsWith(contextPath)) {
                href = contextPath + href;
            }
            handler.replaceAttribute(AttributeNames.forHTMLName("href"), "href", href);
        }
        handler.removeAttribute(getDialectPrefix(), getAttributeName());
    }
}
