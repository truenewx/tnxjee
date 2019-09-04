package org.truenewx.tnxjee.test.spring.context.junit4;

import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.Framework;
import org.truenewx.tnxjee.test.junit.rules.ExpectedBusinessException;
import org.truenewx.tnxjee.test.junit.rules.LogCaption;

/**
 * JUnit4+Spring环境测试
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Framework.class)
public abstract class JUnit4SpringContextTest extends AbstractJUnit4SpringContextTests {

    @Rule
    public LogCaption logCaption = LogCaption.DEFAULT;

    @Rule
    public ExpectedBusinessException expectedBusinessException = ExpectedBusinessException.INSTANCE;

}
