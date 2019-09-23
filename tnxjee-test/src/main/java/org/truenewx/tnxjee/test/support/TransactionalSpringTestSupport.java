package org.truenewx.tnxjee.test.support;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.truenewx.tnxjee.model.Entity;
import org.truenewx.tnxjee.repo.data.DataProviderFactory;

/**
 * 带自动事务的JUnit4+Spring环境测试
 *
 * @author jianglei
 */
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public abstract class TransactionalSpringTestSupport extends SpringTestSupport {

    @Autowired
    private DataProviderFactory dataProviderFactory;

    protected <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        return this.dataProviderFactory.getDataList(entityClass);
    }

    protected <T extends Entity> T getData(Class<T> entityClass, int index) {
        List<T> list = getDataList(entityClass);
        return list == null ? null : list.get(index);
    }

    protected <T extends Entity> T getFirstData(Class<T> entityClass) {
        return getData(entityClass, 0);
    }

    @Before
    public void before() {
        this.dataProviderFactory.init(getEntityClasses());
    }

    @After
    public void after() {
        this.dataProviderFactory.clear(getEntityClasses());
    }

    protected Class<?>[] getEntityClasses() {
        return null;
    }

}
