package org.truenewx.tnxjee.core.version;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Maven版本号读取器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
@ConfigurationProperties(prefix = "project")
public class MavenVersionReader extends AbstractVersionReader {

    private String version;

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    protected String readFullVersion(ApplicationContext context) {
        return this.version;
    }

}
