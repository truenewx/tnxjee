package org.truenewx.tnxjee.test.init;

import java.util.List;

/**
 * 单元测试数据提供者
 */
public interface TestDataProvider {

    <T> List<T> getData(Class<T> entityClass);

}
