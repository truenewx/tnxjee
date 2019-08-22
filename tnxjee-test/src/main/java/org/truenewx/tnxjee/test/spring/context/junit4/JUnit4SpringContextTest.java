package org.truenewx.tnxjee.test.spring.context.junit4;

import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.truenewx.tnxjee.test.junit.rules.ExpectedBusinessException;
import org.truenewx.tnxjee.test.junit.rules.LogCaption;

/**
 * JUnit4+Spring环境测试
 *
 * @author jianglei
 */
public abstract class JUnit4SpringContextTest extends AbstractJUnit4SpringContextTests {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = ExpectedBusinessException.INSTANCE;

}
