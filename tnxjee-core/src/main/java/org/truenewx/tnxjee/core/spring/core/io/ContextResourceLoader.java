package org.truenewx.tnxjee.core.spring.core.io;

import org.springframework.core.io.ContextResource;
import org.springframework.core.io.ResourceLoader;

/**
 * 上下文资源加载器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public interface ContextResourceLoader extends ResourceLoader {

    @Override
    ContextResource getResource(String location);
}
