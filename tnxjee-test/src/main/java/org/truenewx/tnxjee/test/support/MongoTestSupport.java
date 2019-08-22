package org.truenewx.tnxjee.test.support;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.core.util.ClassUtil;
import org.truenewx.tnxjee.model.Entity;
import org.truenewx.tnxjee.test.TnxjeeTestApplication;
import org.truenewx.tnxjee.test.context.config.EmbeddedMongoConfiguration;
import org.truenewx.tnxjee.test.init.TestDataProvider;
import org.truenewx.tnxjee.test.spring.context.junit4.JUnit4SpringContextTest;

/**
 * 基于MongoDb的基础测试支持
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TnxjeeTestApplication.class)
@Import(EmbeddedMongoConfiguration.class)
public abstract class MongoTestSupport<T extends Entity> extends JUnit4SpringContextTest {

    @Autowired
    protected TestDataProvider dataProvider;
    @Autowired
    protected MongoTemplate mongoTemplate;

    protected Class<T> getEntityClass() {
        return ClassUtil.getActualGenericType(getClass(), 0);
    }

    @Before
    public void before() {
        List<T> list = this.dataProvider.getData(getEntityClass());
        if (list != null) {
            list.forEach(entity -> {
                this.mongoTemplate.save(entity);
            });
        }
    }

    @After
    public void after() {
        this.mongoTemplate.dropCollection(getEntityClass());
    }

}
