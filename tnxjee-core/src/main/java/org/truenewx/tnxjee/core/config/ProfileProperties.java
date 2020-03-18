package org.truenewx.tnxjee.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 环境配置属性集
 */
@ConfigurationProperties("tnxjee.profile")
public class ProfileProperties {

    /**
     * 当前环境是否正式环境
     */
    private boolean formal;

    public boolean isFormal() {
        return this.formal;
    }

    public void setFormal(boolean formal) {
        this.formal = formal;
    }

}
