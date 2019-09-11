package org.truenewx.tnxjee.test.data;

import java.util.List;

/**
 * 单元测试数据构建器
 */
public interface TestDataBuilder<T> {

    List<T> build(TestDataProvider provider);

}
