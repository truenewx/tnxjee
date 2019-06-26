package org.truenewx.tnxjee.core.spring.beans;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * 在容器初始化完成后资源加载支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class ContextInitializedResourceLoadSupport implements ContextInitializedBean {

    private String locationPattern;

    public void setLocationPattern(String locationPattern) {
        this.locationPattern = locationPattern;
    }

    @Override
    public void afterInitialized(ApplicationContext context) throws Exception {
        Resource[] resources = context.getResources(this.locationPattern);
        for (Resource resource : resources) {
            loadResource(resource);
        }
    }

    protected abstract void loadResource(Resource resource) throws Exception;

}
