package org.truenewx.tnxjee.web.controller.config;

import java.util.Collection;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.repo.jpa.support.JpaAccessTemplate;
import org.truenewx.tnxjee.repo.support.DataAccessTemplate;
import org.truenewx.tnxjee.web.controller.context.request.DynamicOpenEntityManagerInViewInterceptor;

/**
 * WEB控制层MVC配置支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebControllerMvcConfigurationSupport implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private JpaProperties jpaProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Collection<DataAccessTemplate> templates = this.context.getBeansOfType(DataAccessTemplate.class).values();
        if (templates.size() > 0) {
            Properties jpaProperties = new Properties();
            jpaProperties.putAll(this.jpaProperties.getProperties());
            for (DataAccessTemplate template : templates) {
                if (template instanceof JpaAccessTemplate) {
                    JpaAccessTemplate jst = (JpaAccessTemplate) template;
                    EntityManagerFactory entityManagerFactory = jst.getEntityManagerFactory();
                    DynamicOpenEntityManagerInViewInterceptor interceptor = new DynamicOpenEntityManagerInViewInterceptor();
                    interceptor.setEntityManagerFactory(entityManagerFactory);
                    interceptor.setJpaProperties(jpaProperties);
                    registry.addWebRequestInterceptor(interceptor);
                }
            }
        }
    }

}
