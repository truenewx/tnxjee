package org.truenewx.tnxjee.repo.jpa.hibernate;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.hibernate.engine.transaction.jta.platform.internal.AbstractJtaPlatform;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * JTAp平台实现
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
