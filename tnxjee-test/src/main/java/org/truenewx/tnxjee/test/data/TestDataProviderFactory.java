package org.truenewx.tnxjee.test.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.data.DataProvider;
import org.truenewx.tnxjee.repo.data.DataProviderFactory;
import org.truenewx.tnxjee.repo.support.RepoFactory;

/**
 * 单元测试数据提供者工厂
 */
@Component
public class TestDataProviderFactory implements DataProviderFactory, ApplicationContextAware {
    @Autowired
    private RepoFactory repoFacotory;
    private Map<Class<?>, DataProvider<?>> providers = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        @SuppressWarnings("rawtypes")
        Map<String, DataProvider> beans = applicationContext.getBeansOfType(DataProvider.class);
        beans.values().forEach(provider -> {
            Class<?> entityClass = ClassUtil.getActualGenericType(provider.getClass(),
                    DataProvider.class, 0);
            this.providers.put(entityClass, provider);
        });
    }

    public void init(Class<?>... entityClasses) {
        if (ArrayUtils.isEmpty(entityClasses)) {
            this.providers.values().forEach(provider -> {
                provider.getDataList(this);
            });
        } else {
            for (Class<?> entityClass : entityClasses) {
                DataProvider<?> provider = this.providers.get(entityClass);
                if (provider != null) {
                    provider.getDataList(this);
                }
            }
        }
    }

    @Override
    public <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        DataProvider<T> provider = (DataProvider<T>) this.providers.get(entityClass);
        if (provider != null) {
            return provider.getDataList(this);
        } else {
            Repo<T> repo = this.repoFacotory.getRepoByEntityClass(entityClass);
            if (repo != null) {
                return CollectionUtil.toList(repo.findAll());
            }
        }
        return null;
    }

    public void clear(Class<?>... entityClasses) {
        if (ArrayUtils.isEmpty(entityClasses)) {
            this.providers.values().forEach(provider -> {
                provider.clear();
            });
        } else {
            for (Class<?> entityClass : entityClasses) {
                DataProvider<?> provider = this.providers.get(entityClass);
                if (provider != null) {
                    provider.clear();
                }
            }
        }
    }
}
