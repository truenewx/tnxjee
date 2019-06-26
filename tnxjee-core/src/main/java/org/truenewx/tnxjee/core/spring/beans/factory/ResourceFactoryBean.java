package org.truenewx.tnxjee.core.spring.beans.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.truenewx.tnxjee.core.spring.core.io.WebContextResourceLoader;

/**
 * 资源工厂Bean
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class ResourceFactoryBean implements FactoryBean<Resource> {

    private String location;
    private ResourceLoader resourceLoader = new WebContextResourceLoader();

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public Resource getObject() throws Exception {
        return this.resourceLoader.getResource(this.location);
    }

    @Override
    public Class<Resource> getObjectType() {
        return Resource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
