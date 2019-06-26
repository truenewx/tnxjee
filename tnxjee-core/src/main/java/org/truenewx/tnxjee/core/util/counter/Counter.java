package org.truenewx.tnxjee.core.util.counter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 计数器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface Counter<K> extends Iterable<Entry<K, Integer>> {

    int add(K key, int step);

    Integer remove(K key);

    Integer count(K key);

    int size();

    boolean isEmpty();

    Set<K> keySet();

    Set<Entry<K, Integer>> entrySet();

    void toMap(final Map<K, Integer> map);

    Map<K, Integer> asMap();

}
