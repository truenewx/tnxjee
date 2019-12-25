package org.truenewx.tnxjee.web.controller.util;

import com.aliyun.oss.internal.Mimetypes;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.util.WebUtils;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.LogUtil;
import org.truenewx.tnxjee.core.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Web控制层工具类
 *
 * @author jianglei
 */
public class WebControllerUtil {

    private WebControllerUtil() {
    }

    /**
     * 获取指定request请求中的所有参数的map集合
     *
     * @param request                请求
     * @param excludedParameterNames 排除的参数名
     * @return 指定request请求中的所有参数的map集合
     */
    public static Map<String, Object> getRequestParameterMap(ServletRequest request, String... excludedParameterNames) {
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, String[]> params = request.getParameterMap();
        params.forEach((name, values) -> {
            if (values != null && !ArrayUtils.contains(excludedParameterNames, name)) {
                if (values.length == 1) {
                    map.put(name, values[0]);
                } else {
                    map.put(name, values);
                }
            }
        });
        return map;
    }

    /**
     * 获取指定URL去掉web项目根路径之后的相对路径URL
     *
     * @param request 请求
     * @param url     URL
     * @return 相对于web项目的URL
     */
    public static String getRelativeUrl(HttpServletRequest request, String url) {
        String root = request.getContextPath();
        if (!root.equals(Strings.SLASH) && url.startsWith(root)) {
            url = url.substring(root.length());
        }
        return url;
    }

    /**
     * 获取相对于web项目的请求URL，不包含请求参数串
     *
     * @param request 请求
     * @return 相对于web项目的请求URL
     */
    public static String getRelativeRequestUrl(HttpServletRequest request) {
        return getRelativeUrl(request, request.getRequestURI());
    }

    /**
     * 获取相对于web项目的包含请求参数串的请求URL
     *
     * @param request               请求
     * @param encode                是否进行字符转码
     * @param ignoredParameterNames 不包含在参数串中的参数名清单
     * @return 相对于web项目的请求URL
     */
    public static String getRelativeRequestUrlWithQueryString(HttpServletRequest request, boolean encode,
            String... ignoredParameterNames) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = System.getProperty("file.encoding", Strings.ENCODING_UTF8);
        }
        String url = getRelativeRequestUrl(request);
        String queryString = request.getQueryString();
        if (queryString != null) {
            if (ignoredParameterNames.length > 0) {
                String[] params = queryString.split("&");
                for (int i = 0; i < params.length; i++) {
                    for (String name : ignoredParameterNames) {
                        String prefix = name + "=";
                        if (params[i].startsWith(prefix)) {
                            params[i] = null;
                            break;
                        }
                    }
                    if (params[i] != null && encode) {
                        int index = params[i].indexOf('=');
                        if (index >= 0) {
                            String value = params[i].substring(index + 1);
                            try {
                                value = URLEncoder.encode(value, encoding);
                                params[i] = params[i].substring(0, index + 1) + value;
                            } catch (UnsupportedEncodingException e) {
                                LogUtil.error(WebControllerUtil.class, e);
                            }
                        }
                    }
                }
                queryString = "";
                for (String param : params) {
                    if (param != null) {
                        queryString += param + "&";
                    }
                }
                if (queryString.length() > 0) {
                    queryString = queryString.substring(0, queryString.length() - 1);
                }
            }
            if (queryString.length() > 0) {
                url += "?" + queryString;
            }
        }
        if (encode) {
            int index1 = url.lastIndexOf('/');
            int index2 = url.indexOf('.', index1);
            if (index2 > index1) {
                String tail = url.substring(index1 + 1, index2); // 取得链接的最后一级，并去掉访问后缀

                try {
                    tail = URLEncoder.encode(tail, encoding);
                } catch (UnsupportedEncodingException e) {
                    LogUtil.error(WebControllerUtil.class, e);
                }
                url = url.substring(0, index1 + 1) + tail + url.substring(index2);
            }
        }
        return url;
    }

    /**
     * 获取相对于web项目的请求action。action即请求url中去掉参数和请求后缀之后的部分
     *
     * @param request 请求
     * @return 相对于web项目的请求action
     */
    public static String getRelativeRequestAction(HttpServletRequest request) {
        String url = getRelativeRequestUrl(request);
        return getAction(url);
    }

    /**
     * 从指定URL中截取请求action部分，即请求url中去掉参数和请求后缀之后的部分
     *
     * @param url 请求url
     * @return 请求action
     */
    public static String getAction(String url) {
        int index = url.indexOf("?");
        if (index >= 0) {
            url = url.substring(0, index);
        }
        index = url.lastIndexOf(".");
        if (index >= 0) {
            url = url.substring(0, index);
        }
        return url;
    }

    /**
     * 从指定ServletContext的属性中获取占位符对应值，替换指定字符串中的占位符，返回新的字符串
     *
     * @param s              要替换占位符的字符串
     * @param servletContext the ServletContext
     * @return 替换后的新字符串
     */
    public static String replacePlaceholderFromServletContext(String s, ServletContext servletContext) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        String begin = "${";
        String end = "}";
        String[] placeholders = StringUtil.substringsBetweens(s, begin, end);
        for (String placeholder : placeholders) {
            String key = placeholder.substring(begin.length(), placeholder.length() - end.length());
            Object value = servletContext.getAttribute(key);
            if (value != null) {
                placeholder = "\\$\\{" + key + "\\}";
                s = s.replaceAll(placeholder, value.toString());
            }
        }
        return s;
    }

    private static String getHostByFullUrl(String url) {
        int index = url.indexOf("://");
        url = url.substring(index + 3);
        index = url.indexOf(Strings.SLASH);
        if (index >= 0) {
            url = url.substring(0, index);
        }
        return url;
    }

    private static String getSubDomainByFullUrl(String url, int topDomainLevel) {
        url = getHostByFullUrl(url);
        int index = url.indexOf(":");
        if (index >= 0) {
            url = url.substring(0, index);
        }
        if (StringUtil.isIp(url)) {
            return null;
        }
        String[] domains = url.split("\\.");
        if (topDomainLevel < 2) {
            topDomainLevel = 2;
        }
        if (domains.length > topDomainLevel) {
            String domain = domains[0];
            for (int i = 1; i < domains.length - topDomainLevel; i++) {
                domain += "." + domains[i];
            }
            return domain;
        }
        return null;
    }

    public static String getFootSubDomain(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        url = getHostByFullUrl(url);
        int index = url.indexOf(":");
        if (index >= 0) {
            url = url.substring(0, index);
        }
        if (StringUtil.isIp(url) || "localhost".equals(url)) {
            return null;
        }
        String[] domains = url.split("\\.");
        if (domains.length > 0) {
            return domains[0];
        }
        return null;
    }

    /**
     * 从指定HTTP请求中获取访问的主机地址（域名[:端口]）
     *
     * @param request 指定HTTP请求
     * @return 访问的主机地址
     */
    public static String getHost(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        return getHostByFullUrl(url);
    }

    /**
     * 从指定HTTP请求中获取访问的主机地址（协议://域名[:端口]）
     *
     * @param request 指定请求
     * @return 访问的主机地址含协议
     */
    public static String getProtocolAndHost(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String protocol = "http://";
        if (url.startsWith("https:")) {
            protocol = "https://";
        }
        return protocol + getHostByFullUrl(url);
    }

    /**
     * 从指定HTTP请求中获取访问的子域名。如果访问的URL为IP或非标准分段域名，则返回null
     *
     * @param request        HTTP请求
     * @param topDomainLevel 顶级域名级数，默认为2，个别情况可能大于2，小于2时将被视为2
     * @return 子域名
     */
    public static String getSubDomain(HttpServletRequest request, int topDomainLevel) {
        String url = request.getRequestURL().toString();
        return getSubDomainByFullUrl(url, topDomainLevel);
    }

    /**
     * 从HTTP响应对象中获取图片输出流，该输出流专用于向浏览器客户端输出图片，其它情况请避免使用
     *
     * @param response HTTP响应对象
     * @return 图片输出流，如果出现IO错误则返回null
     */
    public static ServletOutputStream getImageOutputStream(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        try {
            return response.getOutputStream();
        } catch (Exception e) {
            LogUtil.error(WebControllerUtil.class, e);
        }
        return null;
    }

    /**
     * 标准化相对URL地址。所谓标准URL即：所有斜杠均为/，以/开头，不以/结尾<br/>
     * 注意：本方法只能正确处理相对URL地址，绝对路径URL无法正确处理
     *
     * @param url URL
     * @return 标准化后的URL
     */
    public static String standardizeRelativeUrl(String url) {
        url = url.replace('\\', '/');
        if (!url.startsWith(Strings.SLASH)) {
            url = Strings.SLASH + url;
        }
        if (Strings.SLASH.equals(url)) {
            return url;
        }
        if (url.endsWith(Strings.SLASH)) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 读取指定相对于WEB上下文的文件的二进制内容
     *
     * @param context      web上下文
     * @param relativePath 相对于WEB上下文的文件路径
     * @return 指定相对于WEB上下文的文件的二进制内容
     */
    public static byte[] readWebContextFile(ServletContext context, String relativePath) {
        try {
            Resource resource = new ServletContextResource(context, standardizeRelativeUrl(relativePath));
            return FileUtils.readFileToByteArray(resource.getFile());
        } catch (IOException e) {
            LogUtil.error(WebControllerUtil.class, e);
            return new byte[0];
        }
    }

    public static Object removeSessionAttribute(HttpSession session, String name) {
        Object value = session.getAttribute(name);
        if (value != null) {
            session.removeAttribute(name);
        }
        return value;
    }

    /**
     * 解码指定参数
     *
     * @param request HTTP请求
     * @param param   解码前参数
     * @return 解码后参数
     */
    public static String decodeParameter(HttpServletRequest request, String param) {
        if (RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
            String encoding = request.getCharacterEncoding();
            if (encoding == null) {
                encoding = Strings.ENCODING_UTF8;
            }
            try {
                return URLDecoder.decode(param, encoding);
            } catch (UnsupportedEncodingException e) {
                LogUtil.error(WebControllerUtil.class, e); // 编码已确保有效，不应该出现该异常
            }
        }
        return param;
    }

    /**
     * 根据名字获取cookie
     *
     * @param request
     * @param name    cookie名称
     * @return cookie对象
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (StringUtils.isNotBlank(name)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name)) {
                        return cookie;
                    }
                }
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);
        return cookie == null ? null : cookie.getValue();
    }

    /**
     * 获取Map形式的Cookie集
     */
    public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

    /**
     * 创建Cookie对象
     *
     * @param name     名称
     * @param value    值
     * @param maxAge   有效时间，单位：秒
     * @param httpOnly 是否禁止客户端javascript访问
     * @param path     路径
     * @return Cookie对象
     */
    public static Cookie createCookie(String name, String value, int maxAge, boolean httpOnly, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath(path);
        return cookie;
    }

    /**
     * 创建Cookie对象
     *
     * @param name     名称
     * @param value    值
     * @param maxAge   有效时间，单位：秒
     * @param httpOnly 是否禁止客户端javascript访问
     * @param request  请求
     * @return Cookie对象
     */
    public static Cookie createCookie(String name, String value, int maxAge, boolean httpOnly,
            HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (StringUtils.isBlank(contextPath)) {
            contextPath = Strings.SLASH;
        }
        return createCookie(name, value, maxAge, httpOnly, contextPath);
    }

    /**
     * 添加cookie
     *
     * @param request     请求
     * @param response    响应
     * @param cookieName  cookie名称
     * @param cookieValue cookie值
     * @param maxAge      有效时间，单位：秒
     * @author jianglei
     */
    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
            String cookieValue, int maxAge) {
        Cookie cookie = createCookie(cookieName, cookieValue, maxAge, false, request);
        response.addCookie(cookie);
    }

    /**
     * 添加有效期最大的cookie
     *
     * @param request     请求
     * @param response    响应
     * @param cookieName  cookie名称
     * @param cookieValue cookie值
     * @author jianglei
     */
    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName,
            String cookieValue) {
        addCookie(request, response, cookieName, cookieValue, Integer.MAX_VALUE);
    }

    /**
     * 移除cookie
     *
     * @param response 响应
     * @param name     cookie名称
     * @param path     cookie路径
     * @author jianglei
     */
    public static void removeCookie(HttpServletResponse response, String name, String path) {
        Cookie cookie = new Cookie(name, Strings.EMPTY);
        cookie.setPath(path);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 获取include方式的请求URI，如果不是include方式，则返回null
     *
     * @param request 请求
     * @return include方式的请求URI
     * @author jianglei
     */
    public static String getIncludeRequestUri(ServletRequest request) {
        return (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
    }

    /**
     * 获取访问者ip地址<br>
     * 如果使用了nginx，则nginx配置中需添加 proxy_set_header X-Real-IP $remote_addr;
     */
    public static String getRemoteAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index >= 0) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    /**
     * 标准化指定URL中的协议，确保返回的URL包含协议，如果指定URL未包含协议，则返回包含有指定默认协议的URL
     *
     * @param url             URL
     * @param defaultProtocol 默认协议，如："http"
     * @return 包含有协议的URL，如果输入的URL为相对路径，则原样返回
     */
    public static String standardizeUrlProtocol(String url, String defaultProtocol) {
        if (!url.contains("://")) {
            if (url.startsWith("//")) {
                url = defaultProtocol + Strings.COLON + url;
            } else if (!url.startsWith(Strings.SLASH)) {
                url = defaultProtocol + "://" + url;
            }
            // 斜杠开头的为相对URL，不作处理
        }
        if (url.endsWith(Strings.SLASH)) { // 确保不以斜杠结尾
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    /**
     * 标准化指定URL，当该URL不包含协议时，返回包含HTTP协议的URL
     *
     * @param url URL
     * @return 包含有协议（默认为HTTP协议）的URL
     */
    public static String standardizeHttpUrl(String url) {
        return standardizeUrlProtocol(url, "http");
    }

    /**
     * 将request中的所有参数都复制到属性集中
     *
     * @param request 请求
     */
    public static void copyParameters2Attributes(HttpServletRequest request) {
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            request.setAttribute(name, request.getParameter(name));
        }
    }

    public static void setDownloadFilename(HttpServletRequest request, HttpServletResponse response, String filename) {
        response.setContentType(Mimetypes.getInstance().getMimetype(filename));
        String userAgent = request.getHeader("User-Agent").toUpperCase();
        if (userAgent.contains("MSIE") || userAgent.contains("TRIDENT")) {
            filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);
        } else {
            filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        response.setHeader("content-disposition", "attachment;filename=" + filename);
    }

}
