package org.truenewx.tnxjee.repo.jpa.hibernate;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * JTAp平台实现<br>
 * 其类型名称将被指派给Hibernate，Hibernate将创建其实例用来获取{@link TransactionManager}和{@link UserTransaction}实例<br>
 * 这里通过@Component注册一个Spring的bean，在该bean构建时获得Spring容器上下文并存放在类级别属性中，以提供获取上述实例的途径
 *
 * @author jianglei
 */
@Component
public class JtaPlatformImpl extends AbstractJtaPlatform implements ApplicationContextAware {

    private static final long serialVersionUID = -8337718410949973357L;

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        CONTEXT = context;
    }

    @Override
    protected TransactionManager locateTransactionManager() {
        return CONTEXT.getBean(TransactionManager.class);
    }

    @Override
    protected UserTransaction locateUserTransaction() {
        return CONTEXT.getBean(UserTransaction.class);
    }

}
