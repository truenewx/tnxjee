package org.truenewx.tnxjee.web.view.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.util.SpringUtil;
import org.truenewx.tnxjee.web.controller.spring.servlet.mvc.Loginer;
import org.truenewx.tnxjee.web.controller.spring.util.SpringWebUtil;
import org.truenewx.tnxjee.web.controller.util.WebControllerUtil;

/**
 * Web视图层工具类
 *
 * @author jianglei
 */
public class WebViewUtil {

    private WebViewUtil() {
    }

    public static void forward(ServletRequest request, ServletResponse response, String url)
            throws ServletException, IOException {
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * 直接重定向至指定URL。请求将被重置，POST请求参数将丢失，浏览器地址栏显示的URL将更改为指定URL。 URL如果为绝对路径，则必须以http://或https://开头
     *
     * @param url URL
     * @throws IOException 如果重定向时出现IO错误
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException {
        String location = url;
        if (!location.toLowerCase().startsWith("http://") && !location.toLowerCase().startsWith("https://")) {
            if (!location.startsWith(Strings.SLASH)) {
                location = Strings.SLASH + location;
            }
            String webRoot = request.getContextPath();
            if (!location.startsWith(webRoot)) {
                location = webRoot + location;
            }
        }
        response.sendRedirect(location);
    }

    public static String getPreviousUrl(HttpServletRequest request) {
        String prevUrl = getRelativePreviousUrl(request, true);
        if (prevUrl != null) {
            String action = WebControllerUtil.getRelativeRequestAction(request);
            if (prevUrl.startsWith(action)) { // 如果前一页url以当前action开头，则执行默认的前一页规则，以避免跳转相同页
                prevUrl = null;
            } else {
                Loginer loginer = SpringUtil.getFirstBeanByClass(SpringWebUtil.getApplicationContext(request),
                        Loginer.class);
                if (loginer != null && prevUrl != null && loginer.isLoginUrl(prevUrl)) {
                    prevUrl = null;
                }
            }
        }
        return prevUrl;
    }

    /**
     * 获取相对于web项目的前一个请求的URL
     *
     * @param request             请求
     * @param containsQueryString 是否需要包含请求参数
     * @return 前一个请求的URL
     */
    public static String getRelativePreviousUrl(HttpServletRequest request, boolean containsQueryString) {
        String referrer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referrer)) {
            String root = WebControllerUtil.getProtocolAndHost(request);
            String contextPath = request.getContextPath();
            if (!contextPath.equals(Strings.SLASH)) {
                root += contextPath;
            }
            if (referrer.startsWith(root)) {
                String url = referrer.substring(root.length());
                if (!containsQueryString) {
                    int index = url.indexOf("?");
                    if (index > 0) {
                        url = url.substring(0, index);
                    }
                }
                return url;
            }
        }
        return null;
    }

    /**
     * 判断指定请求是否AJAX请求
     *
     * @param request HTTP请求
     * @return 是否AJAX请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

}
