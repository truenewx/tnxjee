package org.truenewx.tnxjee.web.controller.util;

/**
 * 控制层环境属性常量集
 *
 * @author jianglei
 */
public class WebControllerPropertyConstant {

    private WebControllerPropertyConstant() {
    }

    private static final String PREFIX = "tnxjee.web.controller.";

    /**
     * Servlet容器初始化加载的属性集
     */
    public static final String SERVLET_CONTEXT_PROPERTIES = PREFIX + "servlet.context.properties";

}
