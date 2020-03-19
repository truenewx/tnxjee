package org.truenewx.tnxjee.web.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;
import org.truenewx.tnxjee.core.version.VersionReader;
import org.truenewx.tnxjee.web.util.WebControllerPropertyConstant;

/**
 * ServletContext初始化Bean
 */
@Component
public class ServletContextInitializedBean implements ServletContextAware, ContextInitializedBean {
    @Autowired
    private Environment environment;
    @Autowired
    private VersionReader versionReader;
    @Autowired
    private ProfileSupplier profileSupplier;
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    // 在取得ServletContext时可能Spring容器中的Bean尚未完成初始化，所以等容器全部初始化完成后再进行下列动作
    public void afterInitialized(ApplicationContext context) throws Exception {
        Map<String, Object> properties = new HashMap<>();
        String contextProperties = this.environment
                .getProperty(WebControllerPropertyConstant.SERVLET_CONTEXT_PROPERTIES);
        if (StringUtils.isNotBlank(contextProperties)) {
            String[] propertyKeys = contextProperties.split(Strings.COMMA);
            for (String propertyKey : propertyKeys) {
                String value = this.environment.getProperty(propertyKey.trim());
                Assert.notNull(value, "The property '" + propertyKey + "' is required, but it is null.");
                properties.put(propertyKey, value);
            }
        }
        this.servletContext.setAttribute("properties", properties);

        this.servletContext.setAttribute("version", this.versionReader.getVersion());

        boolean formalProfile = this.profileSupplier.isFormal();
        this.servletContext.setAttribute("formalProfile", formalProfile);
        this.servletContext.setAttribute("resourceMin", formalProfile ? ".min" : Strings.EMPTY);

        String contextPath = this.servletContext.getContextPath();
        if (Strings.SLASH.equals(contextPath)) {
            contextPath = Strings.EMPTY;
        }
        this.servletContext.setAttribute("context", contextPath);
    }

}
