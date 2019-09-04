package org.truenewx.tnxjee.test.support;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.truenewx.tnxjee.test.TnxjeeTestModule;
import org.truenewx.tnxjee.test.spring.context.junit4.TransactionalJUnit4SpringContextTest;

/**
 * 基于JPA的基础测试支持
 *
 * @author jianglei
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TnxjeeTestModule.class)
public abstract class JpaTestSupport extends TransactionalJUnit4SpringContextTest {
}
