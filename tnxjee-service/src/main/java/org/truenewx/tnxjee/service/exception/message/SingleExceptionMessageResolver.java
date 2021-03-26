package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.truenewx.tnxjee.service.exception.SingleException;

/**
 * 单异常消息解决器
 *
 * @author jianglei
 */
public interface SingleExceptionMessageResolver {

    /**
     * 解析指定异常错误码得到异常消息
     *
     * @param code   异常错误码
     * @param locale 区域
     * @param args   异常参数
     * @return 异常消息
     */
    String resolveMessage(String code, Locale locale, Object... args);

    /**
     * 解析指定单异常得到异常消息
     *
     * @param se     单异常
     * @param locale 区域
     * @return 异常消息
     */
    String resolveMessage(SingleException se, Locale locale);

}
