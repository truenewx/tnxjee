package org.truenewx.tnxjee.test.support;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.test.TnxjeeTestModule;
import org.truenewx.tnxjee.test.context.config.EmbeddedMongoConfiguration;
import org.truenewx.tnxjee.test.init.TestDataProvider;
import org.truenewx.tnxjee.test.spring.context.junit4.JUnit4SpringContextTest;

/**
 * 基于MongoDb的基础测试支持
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TnxjeeTestModule.class)
@Import(EmbeddedMongoConfiguration.class)
public abstract class MongoTestSupport extends JUnit4SpringContextTest {

    @Autowired
    private TestDataProvider dataProvider;
    @Autowired
    protected MongoTemplate mongoTemplate;

    protected <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        return this.dataProvider.getData(entityClass);
    }

    protected <T extends Entity> T getData(Class<T> entityClass, int index) {
        List<T> list = getDataList(entityClass);
        return list == null ? null : list.get(index);
    }

    protected <T extends Entity> T getFirstData(Class<T> entityClass) {
        return getData(entityClass, 0);
    }

    protected Class<?>[] getInitEntityClasses() {
        return null;
    }

    @Before
    public void before() {
        Class<?>[] entityClasses = getInitEntityClasses();
        if (ArrayUtils.isNotEmpty(entityClasses)) {
            for (Class<?> entityClass : entityClasses) {
                List<?> list = this.dataProvider.getData(entityClass);
                if (list != null) {
                    list.forEach(entity -> {
                        this.mongoTemplate.save(entity);
                    });
                }
            }
        }
    }

    @After
    public void after() {
        Class<?>[] entityClasses = getInitEntityClasses();
        if (ArrayUtils.isNotEmpty(entityClasses)) {
            for (Class<?> entityClass : entityClasses) {
                this.mongoTemplate.dropCollection(entityClass);
            }
        }
    }

}
