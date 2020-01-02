package org.truenewx.tnxjee.test.util;

/**
 * 单元测试工具类
 *
 * @author jianglei
 */
public class TestUtil {
    /**
     * 是否单元测试环境中
     */
    private static Boolean TESTING;

    private TestUtil() {
    }

    /**
     *
     * @return 是否单元测试环境中
     *
     * @author jianglei
     */
    public static boolean isTesting() {
        if (TESTING == null) {
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTrace : stackTraces) {
                String stackString = stackTrace.toString();
                if (stackString.indexOf("junit.runners") >= 0) {
                    TESTING = true;
                    return TESTING;
                }
            }
            TESTING = false;
        }
        return TESTING;
    }

}
