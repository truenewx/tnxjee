package org.truenewx.tnxjee.test.spring.context.junit4;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.test.junit.rules.ExpectedBusinessException;
import org.truenewx.tnxjee.test.junit.rules.LogCaption;

/**
 * 带自动事务的JUnit4+Spring环境测试
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Framework.class)
public abstract class TransactionalJUnit4SpringContextTest
        extends AbstractTransactionalJUnit4SpringContextTests {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = ExpectedBusinessException.INSTANCE;

}
