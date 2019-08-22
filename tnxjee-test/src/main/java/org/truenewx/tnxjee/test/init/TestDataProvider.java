package org.truenewx.tnxjee.test.init;

import java.util.List;

/**
 * 单元测试数据提供者
 */
public interface TestDataProvider {

    <T> List<T> getData(Class<T> entityClass);

    default <T> T get(Class<T> entityClass, int index) {
        List<T> list = getData(entityClass);
        return list == null ? null : list.get(index);
    }

    default <T> T getFirst(Class<T> entityClass) {
        return get(entityClass, 0);
    }

}
