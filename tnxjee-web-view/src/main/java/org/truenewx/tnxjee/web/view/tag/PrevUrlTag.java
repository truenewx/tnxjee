package org.truenewx.tnxjee.web.view.tag;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.web.controller.spring.context.SpringWebContext;
import org.truenewx.tnxjee.web.controller.spring.servlet.mvc.Loginer;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.view.thymeleaf.model.ThymeleafElementTagContext;
import org.truenewx.tnxjee.web.view.thymeleaf.processor.ThymeleafHtmlTagSupport;
import org.truenewx.tnxjee.web.view.util.WebViewUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 输出前一个请求URL的标签
 *
 * @author jianglei
 */
@Component
public class PrevUrlTag extends ThymeleafHtmlTagSupport {

    @Override
    protected String getTagName() {
        return "prev-url";
    }

    @Override
    protected void doProcess(ThymeleafElementTagContext context,
            IElementTagStructureHandler handler) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("type", "hidden");
        attributes.put("name", context.getAttributeValue("name", "prev"));
        String prevUrl = WebViewUtil.getPrevUrl(SpringWebContext.getRequest());
        if (prevUrl != null) {
            attributes.put("value", prevUrl);
        }
        IModelFactory modelFactory = context.getModelFactory();
        IModel model = modelFactory.createModel();
        model.add(modelFactory.createStandaloneElementTag("input", attributes,
                AttributeValueQuotes.DOUBLE, false, false));
        handler.replaceWith(model, false);
    }
}
