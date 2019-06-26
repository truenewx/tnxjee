package org.truenewx.tnxjee.core.model;

import java.io.Serializable;

/**
 * 被切分的
 *
 * @author jianglei
 * @since JDK 1.8
 * @param <S>
 *            切分者类型
 */
public interface Sliced<S extends Serializable> {
    /**
     *
     * @return 切分者
     */
    S getSlicer();
}
