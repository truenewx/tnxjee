package org.truenewx.tnxjee.test.service.support;

import org.junit.Rule;
import org.truenewx.tnxjee.test.repo.support.RepoSpringTestSupport;
import org.truenewx.tnxjee.test.service.junit.rules.ExpectedBusinessException;

/**
 * Service的JUnit4+Spring环境测试
 *
 * @author jianglei
 */
public abstract class ServiceSpringTestSupport extends RepoSpringTestSupport {

    @Rule
    public ExpectedBusinessException expectedBusinessException = ExpectedBusinessException.INSTANCE;

}
