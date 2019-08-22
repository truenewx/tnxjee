package org.truenewx.tnxjee.test.init;

import java.util.List;
import java.util.function.Supplier;

/**
 * 单元测试数据供应者
 */
public interface TestDataSupplier<T> extends Supplier<List<T>> {

    Class<T> getEntityClass();

}
