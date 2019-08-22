package org.truenewx.tnxjee.test.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 测试数据初始化工厂实现
 */
@Component
public class TestDataProviderImpl implements TestDataProvider, ApplicationContextAware {

    private Map<Class<?>, TestDataSupplier<?>> suppliers = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        @SuppressWarnings("rawtypes")
        Map<String, TestDataSupplier> beans = applicationContext
                .getBeansOfType(TestDataSupplier.class);
        beans.values().forEach(supplier -> {
            this.suppliers.put(supplier.getEntityClass(), supplier);
        });
    }

    @Override
    public <T> List<T> getData(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        TestDataSupplier<T> supplier = (TestDataSupplier<T>) this.suppliers.get(entityClass);
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }
}
