package org.truenewx.tnxjee.web.servlet;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.function.ProfileSupplier;
import org.truenewx.tnxjee.core.version.VersionReader;

/**
 * ServletContext初始化Bean
 */
@Component
@EnableConfigurationProperties(ServletContextProperties.class)
public class ServletContextInitializedBean implements ServletContextAware, ContextInitializedBean {
    @Autowired
    private VersionReader versionReader;
    @Autowired
    private ProfileSupplier profileSupplier;
    @Autowired(required = false)
    private ServletContextProperties properties;
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    // 在取得ServletContext时可能Spring容器中的Bean尚未完成初始化，所以等容器全部初始化完成后再进行下列动作
    public void afterInitialized(ApplicationContext context) throws Exception {
        if (this.properties != null) {
            Map<String, String> attributes = this.properties.getAttributes();
            if (attributes != null) {
                attributes.forEach((key, value) -> {
                    this.servletContext.setAttribute(key, value);
                });
            }
        }

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