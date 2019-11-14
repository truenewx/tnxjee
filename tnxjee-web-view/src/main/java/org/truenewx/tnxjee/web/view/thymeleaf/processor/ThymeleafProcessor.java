package org.truenewx.tnxjee.web.view.thymeleaf.processor;

import org.thymeleaf.processor.IProcessor;
import org.truenewx.tnxjee.web.view.thymeleaf.dialect.ThymeleafDialect;

/**
 * 扩展的Thymeleaf处理器
 */
public interface ThymeleafProcessor extends IProcessor {

    /**
     * 获取所属方言的前缀
     *
     * @return 所属方言的前缀
     */
    default String getDialectPrefix() {
        return ThymeleafDialect.PREFIX;
    }

}
