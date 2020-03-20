package org.truenewx.tnxjee.test.service.support;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.model.entity.Entity;
import org.truenewx.tnxjee.test.service.data.DataProviderFactory;
import org.truenewx.tnxjee.test.service.junit.rules.ExpectedBusinessException;
import org.truenewx.tnxjee.test.support.SpringTestSupport;

/**
 * Service的JUnit4+Spring环境测试
 *
 * @author jianglei
 */
public abstract class ServiceSpringTestSupport extends SpringTestSupport {

    @Rule
    public ExpectedBusinessException expectedBusinessException = ExpectedBusinessException.INSTANCE;

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
