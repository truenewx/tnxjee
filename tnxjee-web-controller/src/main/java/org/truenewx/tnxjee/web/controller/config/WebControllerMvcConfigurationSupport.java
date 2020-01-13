package org.truenewx.tnxjee.web.controller.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.truenewx.tnxjee.repo.jpa.support.JpaSchemaTemplate;
import org.truenewx.tnxjee.repo.support.SchemaTemplate;
import org.truenewx.tnxjee.web.controller.context.request.DynamicOpenEntityManagerInViewInterceptor;

import javax.persistence.EntityManagerFactory;
import java.util.Collection;

/**
 * WEB控制层MVC配置支持，可选的控制层配置均在此配置支持体系中
 *
 * @author jianglei
 */
public abstract class WebControllerMvcConfigurationSupport implements WebMvcConfigurer {

    @Autowired
    private ApplicationContext context;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Collection<SchemaTemplate> templates = this.context.getBeansOfType(SchemaTemplate.class).values();
        for (SchemaTemplate template : templates) {
            if (template instanceof JpaSchemaTemplate) {
                JpaSchemaTemplate jst = (JpaSchemaTemplate) template;
                EntityManagerFactory entityManagerFactory = jst.getEntityManager().getEntityManagerFactory();
                DynamicOpenEntityManagerInViewInterceptor interceptor = new DynamicOpenEntityManagerInViewInterceptor();
                interceptor.setEntityManagerFactory(entityManagerFactory);
                registry.addWebRequestInterceptor(interceptor);
            }
        }
    }

}
