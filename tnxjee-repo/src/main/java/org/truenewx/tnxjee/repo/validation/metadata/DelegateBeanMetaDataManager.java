package org.truenewx.tnxjee.repo.validation.metadata;

import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;

import java.util.Collections;

public class DelegateBeanMetaDataManager extends BeanMetaDataManager {

    private BeanMetaDataManager delegate;

    public DelegateBeanMetaDataManager(BeanMetaDataManager delegate) {
        super(null, null, null, null, null, null, Collections.emptyList(), null);
        this.delegate = delegate;
    }

    @Override
    public <T> BeanMetaData<T> getBeanMetaData(Class<T> beanClass) {
        return this.delegate.getBeanMetaData(beanClass);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public int numberOfCachedBeanMetaDataInstances() {
        return this.delegate.numberOfCachedBeanMetaDataInstances();
    }

}
