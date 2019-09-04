package org.truenewx.tnxjee.test.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;
import org.truenewx.tnxjee.repo.support.RepoFactory;
import org.truenewx.tnxjee.test.spring.context.junit4.TransactionalJUnit4SpringContextTest;

/**
 * 基于JPA的基础测试支持
 *
 * @author jianglei
 */
public abstract class JpaTestSupport extends TransactionalJUnit4SpringContextTest {

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
