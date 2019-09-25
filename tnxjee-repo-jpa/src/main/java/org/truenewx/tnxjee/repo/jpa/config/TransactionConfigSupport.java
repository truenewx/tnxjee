package org.truenewx.tnxjee.repo.jpa.config;

import java.util.Properties;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.truenewx.tnxjee.core.spring.context.properties.EnvironmentPropertiesProvider;

import com.atomikos.icatch.config.UserTransactionService;
import com.atomikos.icatch.config.UserTransactionServiceImp;
import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * 事务配置支持
 *
 * @author jianglei
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfigSupport {

    @Autowired
    private EnvironmentPropertiesProvider propertiesProvider;

    public UserTransactionService userTransactionService() {
        Properties properties = this.propertiesProvider.getProperties("com.atomikos.icatch", true);
        return new UserTransactionServiceImp(properties);
    }

    public UserTransaction userTransaction() throws Exception {
        UserTransactionImp userTransaction = new UserTransactionImp();
        userTransaction.setTransactionTimeout(300);
        return userTransaction;
    }

    public TransactionManager atomikosTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    public PlatformTransactionManager transactionManager() throws Exception {
        JtaTransactionManager transactionManager = new JtaTransactionManager(userTransaction(),
                atomikosTransactionManager());
        transactionManager.setAllowCustomIsolationLevels(true);
        return transactionManager;
    }

}