package org.truenewx.tnxjee.core.util.counter;

import java.util.TreeMap;

/**
 * 基于TreeMap的计数器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class TreeCounter<K> extends AbstractCounter<K> {

    public TreeCounter() {
        super(new TreeMap<>());
    }

}
