package org.truenewx.tnxjee.repo.support;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.beans.ContextInitializedBean;

/**
 * 数据库模式访问模板工厂实现
 *
 * @author jianglei
 */
@Component
public class SchemaTemplateFactoryImpl implements SchemaTemplateFactory, ContextInitializedBean {

    private Map<Class<?>, SchemaTemplate> templateMappings = new HashMap<>();

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        context.getBeansOfType(SchemaTemplate.class).values().forEach(template -> {
            template.getEntityClasses().forEach(entityClass -> {
                this.templateMappings.put(entityClass, template);
            });
        });
    }

    @Override
    public SchemaTemplate getSchemaTemplate(Class<?> entityClass) {
        return this.templateMappings.get(entityClass);
    }

}
