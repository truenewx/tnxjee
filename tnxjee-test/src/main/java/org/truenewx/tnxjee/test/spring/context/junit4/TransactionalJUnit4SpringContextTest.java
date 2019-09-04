package org.truenewx.tnxjee.test.spring.context.junit4;

import java.util.List;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.TnxjeeRepoModule;
import org.truenewx.tnxjee.repo.support.RepoFactory;
import org.truenewx.tnxjee.test.junit.rules.ExpectedBusinessException;
import org.truenewx.tnxjee.test.junit.rules.LogCaption;

/**
 * 带自动事务的JUnit4+Spring环境测试
 *
 * @author jianglei
 */
@ContextConfiguration(classes = TnxjeeRepoModule.class)
public abstract class TransactionalJUnit4SpringContextTest
        extends AbstractTransactionalJUnit4SpringContextTests {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = ExpectedBusinessException.INSTANCE;

    @Autowired
    private RepoFactory repoFactory;

    protected <T extends Entity> List<T> getDataList(Class<T> entityClass) {
        Repo<T> repo = this.repoFactory.getRepoByEntityClass(entityClass);
        return repo.findAll();
    }

    protected <T extends Entity> T getData(Class<T> entityClass, int index) {
        List<T> list = getDataList(entityClass);
        return list == null ? null : list.get(index);
    }

    protected <T extends Entity> T getFirstData(Class<T> entityClass) {
        Repo<T> repo = this.repoFactory.getRepoByEntityClass(entityClass);
        return repo.first();
    }

}
