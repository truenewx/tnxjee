package org.truenewx.tnxjee.core.version;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.core.env.ProfileSupplier;

/**
 * 默认版本号获取器
 */
@Component
@ConfigurationProperties(prefix = "tnxjee.version")
public class DefaultVersionGetter implements VersionGetter, ApplicationContextAware {
    @Autowired
    private VersionReader versionReader;
    private ApplicationContext context;
    private String[] formalProfiles = {"product"};

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public void setFormalProfile(String formalProfile) {
        this.formalProfiles = formalProfile.split(Strings.COMMA);
    }

    @Override
    public String getVersion() {
        String profile = ProfileSupplier.getProfile(this.context);
        boolean withBuild = !ArrayUtils.contains(this.formalProfiles, profile);
        return this.versionReader.getVersion(withBuild);
    }
}


