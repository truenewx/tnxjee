package org.truenewx.tnxjee.test.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.core.util.CollectionUtil;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.support.RepoFactory;

/**
 * 单元测试数据提供者实现
 */
@Component
public class TestDataProviderImpl implements TestDataProvider, ApplicationContextAware {
    @Autowired
    private RepoFactory repoFacotory;
    @Autowired(required = false)
    private MongoOperations mongoOperations;
    private Map<Class<?>, TestDataBuilder<?>> builders = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        @SuppressWarnings("rawtypes")
        Map<String, TestDataBuilder> beans = applicationContext
                .getBeansOfType(TestDataBuilder.class);
        beans.values().forEach(builder -> {
            Class<?> entityClass = ClassUtil.getActualGenericType(builder.getClass(),
                    TestDataBuilder.class, 0);
            this.builders.put(entityClass, builder);
        });
    }

    @Override
    public <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        TestDataBuilder<?> builder = this.builders.get(entityClass);
        if (builder != null) { // 存在实体数据初始化器，则使用mongodb存取该实体的初始数据
            if (!this.mongoOperations.collectionExists(entityClass)) {
                init(builder);
            }
            return this.mongoOperations.findAll(entityClass);
        } else {
            Repo<T> repo = this.repoFacotory.getRepoByEntityClass(entityClass);
            if (repo != null) {
                return CollectionUtil.toList(repo.findAll());
            }
        }
        return null;
    }

    private void init(TestDataBuilder<?> builder) {
        builder.build(this).forEach(entity -> {
            this.mongoOperations.save(entity);
        });
    }

    @Override
    public void reset(Class<?>... entityClasses) {
        if (ArrayUtils.isEmpty(entityClasses)) {
            this.builders.keySet().forEach(entityClass -> {
                reset(entityClass);
            });
        } else {
            for (Class<?> entityClass : entityClasses) {
                reset(entityClass);
            }
        }
    }

    private void reset(Class<?> entityClass) {
        this.mongoOperations.dropCollection(entityClass);
        init(this.builders.get(entityClass));
    }
}
