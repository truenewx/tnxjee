package org.truenewx.tnxjee.web.controller.context.request;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate5.support.OpenSessionInViewInterceptor;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.truenewx.tnxjee.repo.jpa.support.JpaSchemaTemplate;
import org.truenewx.tnxjee.repo.support.SchemaTemplate;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 复合OpenSessionInView拦截器
 */
public class CompositeOpenSessionInViewInterceptor implements AsyncWebRequestInterceptor, ApplicationContextAware {

    private List<OpenSessionInViewInterceptor> interceptors = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Collection<SchemaTemplate> templates = context.getBeansOfType(SchemaTemplate.class).values();
        for (SchemaTemplate template : templates) {
            if (template instanceof JpaSchemaTemplate) {
                JpaSchemaTemplate jst = (JpaSchemaTemplate) template;
                EntityManagerFactory entityManagerFactory = jst.getEntityManager().getEntityManagerFactory();
                if (entityManagerFactory instanceof SessionFactory) {
                    SessionFactory sessionFactory = (SessionFactory) entityManagerFactory;
                    OpenSessionInViewInterceptor interceptor = new OpenSessionInViewInterceptor();
                    interceptor.setSessionFactory(sessionFactory);
                    this.interceptors.add(interceptor);
                }
            }
        }
    }

    public boolean isAvailable() {
        return this.interceptors.size() > 0;
    }

    @Override
    public void preHandle(WebRequest request) throws Exception {
        // 正向遍历拦截器清单
        for (OpenSessionInViewInterceptor interceptor : this.interceptors) {
            interceptor.preHandle(request);
        }
    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        // 反向遍历拦截器清单
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            this.interceptors.get(i).postHandle(request, model);
        }
    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        // 反向遍历拦截器清单
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            this.interceptors.get(i).afterCompletion(request, ex);
        }
    }

    @Override
    public void afterConcurrentHandlingStarted(WebRequest request) {
        // 反向遍历拦截器清单
        for (int i = this.interceptors.size() - 1; i >= 0; i--) {
            this.interceptors.get(i).afterConcurrentHandlingStarted(request);
        }
    }

}
