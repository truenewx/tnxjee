package org.truenewx.tnxjee.core.util.counter;

import java.util.LinkedHashMap;

/**
 * 基于LinkedHashMap的计数器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class LinkedHashCounter<K> extends AbstractCounter<K> {

    public LinkedHashCounter() {
        super(new LinkedHashMap<>());
    }

}
