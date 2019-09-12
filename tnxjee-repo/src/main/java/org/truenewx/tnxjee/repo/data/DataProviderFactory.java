package org.truenewx.tnxjee.repo.data;

/**
 * 数据提供者工厂
 */
public interface DataProviderFactory extends DataPool {

    void init(Class<?>... entityClasses);

    void clear(Class<?>... entityClasses);
}
