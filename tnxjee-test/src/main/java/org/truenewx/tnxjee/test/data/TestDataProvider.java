package org.truenewx.tnxjee.test.data;

import java.util.List;

import org.truenewx.tnxjee.model.definition.Entity;

/**
 * 单元测试数据提供者
 */
public interface TestDataProvider {

    <T extends Entity> List<T> getDataList(Class<T> entityClass);

    void reset(Class<?>... entityClasses);

}
