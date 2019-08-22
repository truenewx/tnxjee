package org.truenewx.tnxjee.test.support;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.test.TnxjeeTestApplication;
import org.truenewx.tnxjee.test.spring.context.junit4.TransactionalJUnit4SpringContextTest;

/**
 * 基于JPA的基础测试支持
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = TnxjeeTestApplication.class)
public abstract class JpaTestSupport extends TransactionalJUnit4SpringContextTest {
}
