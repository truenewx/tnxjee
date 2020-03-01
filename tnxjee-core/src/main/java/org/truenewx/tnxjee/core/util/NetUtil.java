package org.truenewx.tnxjee.core.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.truenewx.tnxjee.core.Strings;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络工具类
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NetUtil {

    private NetUtil() {
    }

    /**
     * 获取指定主机名（域名）对应的IP地址
     *
     * @param host 主机名（域名）
     * @return IP地址
     */
    public static String getIpByHost(String host) {
        if (StringUtil.isIp(host)) {
            return host;
        }
        String s = "";
        try {
            InetAddress address = InetAddress.getByName(host);
            for (byte b : address.getAddress()) {
                s += (b & 0xff) + ".";
            }
            if (s.length() > 0) {
                s = s.substring(0, s.length() - 1);
            }
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
        return s;
    }

    /**
     * 获取本机网卡IP地址
     *
     * @return 本机网卡IP地址
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    String ip = ias.nextElement().getHostAddress();
                    if (NetUtil.isIntranetIp(ip) && !"127.0.0.1".equals(ip)) {
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            LogUtil.error(NetUtil.class, e);
        }
        return "127.0.0.1";
    }

    /**
     * 获取本机的公网ip，耗时约为700ms
     *
     * @return 本机的公网ip
     */
    public static String getLocalPublicIp() {
        String content = null;
        try {
            URL url = new URL("http://ip.chinaz.com");
            content = IOUtils.toString(url, Strings.ENCODING_UTF8);
            Pattern pattern = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取指定ip地址字符串转换成的IPv4网络地址对象。如果无法转换则返回null
     *
     * @param ip ip地址字符串
     * @return IPv4网络地址对象
     */
    public static Inet4Address getInet4Address(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address instanceof Inet4Address) {
                return (Inet4Address) address;
            }
        } catch (UnknownHostException e) {
        }
        return null;
    }

    /**
     * 判断指定字符串是否内网IP地址
     *
     * @param s 字符串
     * @return true if 指定字符串是内网IP地址, otherwise false
     */
    public static boolean isIntranetIp(String s) {
        if (StringUtil.isIp(s)) {
            if (s.startsWith("192.168.") || s.startsWith("10.") || s.equals("127.0.0.1")
                    || s.equals("0:0:0:0:0:0:0:1")) {
                return true;
            } else if (s.startsWith("172.")) { // 172.16-172.31网段
                String seg = s.substring(4, s.indexOf('.', 4)); // 取第二节
                int value = MathUtil.parseInt(seg);
                if (16 <= value && value <= 31) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定网络地址是否局域网地址
     *
     * @param address 网络地址
     * @return 指定网络地址是否局域网地址
     */
    public static boolean isLanAddress(InetAddress address) {
        byte[] b = address.getAddress();
        // 暂只考虑IPv4
        return b.length == 4 && ((b[0] == 192 && b[1] == 168) || b[0] == 10
                || (b[0] == 172 && b[1] >= 16 && b[1] <= 31)
                || (b[0] == 127 && b[1] == 0 && b[2] == 0 && b[3] == 1));
    }

    /**
     * 获取指定IP地址的整数表达形式
     *
     * @param address IP地址
     * @return 整数表达形式
     */
    public static int intValueOf(InetAddress address) {
        // IPv4和IPv6的hashCode()即为其整数表达形式，本方法向调用者屏蔽该逻辑
        return address.hashCode();
    }

    /**
     * 将指定参数集合转换为参数字符串，形如: a=1&b=true
     *
     * @param params   参数集合
     * @param encoding 字符编码
     * @return 参数字符串
     */
    private static String map2Params(Map<String, Object> params, String encoding) {
        StringBuffer result = new StringBuffer();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                if (value instanceof Collection) {
                    for (Object o : (Collection<Object>) value) {
                        result.append(key).append(Strings.EQUAL).append(encodeParam(o, encoding))
                                .append(Strings.AND);
                    }
                } else if (value instanceof Object[]) {
                    for (Object o : (Object[]) value) {
                        result.append(key).append(Strings.EQUAL).append(encodeParam(o, encoding))
                                .append(Strings.AND);
                    }
                } else {
                    result.append(key).append(Strings.EQUAL).append(encodeParam(value, encoding))
                            .append(Strings.AND);
                }
            }
        }
        if (result.length() > 0) {
            result.delete(result.length() - Strings.AND.length(), result.length());
        }
        return result.toString();
    }

    private static String encodeParam(Object param, String encoding) {
        if (StringUtils.isNotBlank(encoding)) {
            try {
                return URLEncoder.encode(param.toString(), encoding);
            } catch (UnsupportedEncodingException e) {
            }
        }
        // 编码为空或不被支持，则不做编码转换
        return param.toString();
    }

    /**
     * 将指定参数字符串（形如: a=1&b=true）转换为Map参数集合
     *
     * @param paramString 参数字符串
     * @return 参数集合
     */
    private static Map<String, Object> paramString2Map(String paramString) {
        Map<String, Object> map = new HashMap<>();
        String[] pairArray = paramString.split(Strings.AND);
        for (String pair : pairArray) {
            String[] entry = pair.split(Strings.EQUAL);
            if (entry.length == 2) {
                String key = entry[0];
                Object value = map.get(key);
                if (value instanceof Collection) {
                    Collection<Object> collection = (Collection<Object>) value;
                    collection.add(entry[1]);
                } else if (value != null) {
                    Collection<Object> collection = new ArrayList<>();
                    collection.add(entry[1]);
                    map.put(key, collection);
                } else {
                    map.put(key, entry[1]);
                }
            }
        }
        return map;
    }

    /**
     * 将指定参数集合中的参数与指定URL中的参数合并，返回新的URL
     *
     * @param url      URL
     * @param params   参数集合
     * @param encoding 字符编码
     * @return 合并之后的新URL
     * @throws UnsupportedEncodingException 字符编码错误
     */
    public static String mergeParams(String url, Map<String, Object> params, String encoding) {
        String[] pair = url.split("\\?");
        url = pair[0];
        String paramString = "";
        if (pair.length == 2) {
            paramString = pair[1];
        }
        Map<String, Object> paramMap = paramString2Map(paramString);
        paramMap.putAll(params);
        paramString = map2Params(paramMap, encoding);
        return url + "?" + paramString;
    }

    private static String getQueryString(Map<String, Object> params) {
        String result = mergeParams(Strings.EMPTY, params, Strings.ENCODING_UTF8);
        if (result.length() > 0) {
            return result.substring(1); // 去掉首部问号
        }
        return Strings.EMPTY;
    }

    /**
     * 下载指定URL和参数表示的资源到指定本地文件
     *
     * @param url       下载资源链接
     * @param params    下载资源链接的参数
     * @param localFile 本地文件
     */
    public static void download(String url, Map<String, Object> params, File localFile) {
        url = mergeParams(url, params, Strings.ENCODING_UTF8);
        InputStream in = null;
        OutputStream out = null;
        try {
            URL urlObj = new URL(url);
            in = urlObj.openStream();
            IOUtil.createFile(localFile);
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            IOUtil.writeAll(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            LogUtil.error(NetUtil.class, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                LogUtil.error(NetUtil.class, e);
            }
        }
    }

    /**
     * 从后台向指定URL地址发送GET方式请求，获取响应结果
     *
     * @param url    URL地址
     * @param params 请求参数
     * @return 响应结果
     */
    public static String requestByGet(String url, Map<String, Object> params, String encoding) {
        if (StringUtils.isBlank(encoding)) {
            encoding = Strings.ENCODING_UTF8;
        }
        url = mergeParams(url, params, encoding);
        String result = Strings.EMPTY;
        InputStream in = null;
        try {
            URL urlObj = new URL(url);
            in = urlObj.openStream();
            result = IOUtils.toString(in, encoding);
        } catch (IOException e) {
            LogUtil.error(NetUtil.class, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtil.error(NetUtil.class, e);
                }
            }
        }
        return new String(result);
    }

    public static String requestByPost(String url, Map<String, Object> params, String encoding) {
        InputStream in = null;
        PrintWriter out = null;
        String response = "";
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if (StringUtils.isBlank(encoding)) {
                encoding = Strings.ENCODING_UTF8;
            }
            connection.setRequestProperty("contentType", "text/html;charset=" + encoding);
            out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), encoding));
            out.write(getQueryString(params));
            out.flush();
            in = connection.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            response = new String(b, encoding);
        } catch (IOException e) {
            LogUtil.error(NetUtil.class, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtil.error(NetUtil.class, e);
                }
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
        }
        return response;
    }
}
