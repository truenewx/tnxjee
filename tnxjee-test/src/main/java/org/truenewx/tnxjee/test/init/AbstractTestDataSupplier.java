package org.truenewx.tnxjee.test.init;

import java.util.ArrayList;
import java.util.List;

import org.truenewx.tnxjee.core.util.ClassUtil;

/**
 * 抽象的单元测试数据供应者
 *
 * @author jianglei
 */
public abstract class AbstractTestDataSupplier<T> implements TestDataSupplier<T> {

    private List<T> data;

    @Override
    public Class<T> getEntityClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    @Override
    public List<T> get() {
        if (this.data == null) {
            this.data = new ArrayList<T>();
            create(this.data);
        }
        return this.data;
    }

    /**
     * 创建数据并添加到指定集合中
     *
     * @param list 存放数据的集合
     */
    protected abstract void create(List<T> list);

}
