package org.truenewx.tnxjee.web.controller.version;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.version.VersionGetter;

/**
 * ServletContext初始化器
 */
@Component
public class ServletContextInitializer implements ServletContextAware, ContextInitializedBean {
    @Autowired
    private VersionGetter versionGetter;
    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        // 在取得ServletContext时可能VersionGetter尚未完成初始化，所以等容器全部初始化完成后再进行下列动作
        this.servletContext.setAttribute("version", this.versionGetter.getVersion());

        String contextPath = this.servletContext.getContextPath();
        if (Strings.SLASH.equals(contextPath)) {
            contextPath = Strings.EMPTY;
        }
        this.servletContext.setAttribute("context", contextPath);
    }

}
