package org.truenewx.tnxjee.service.exception.message;

import java.util.Locale;

import org.truenewx.tnxjee.service.exception.SingleException;

/**
 * 单异常消息解决器
 *
 * @author jianglei
 */
public interface SingleExceptionMessageResolver<E extends SingleException> {
    /**
     * 解析指定单异常得到异常消息
     *
     * @param e      单异常
     * @param locale 区域
     * @return 异常消息
     */
    String resolveMessage(E e, Locale locale);
}
