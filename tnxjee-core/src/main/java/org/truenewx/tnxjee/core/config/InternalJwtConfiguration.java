package org.truenewx.tnxjee.core.config;

/**
 * 内部JWT配置
 */
public class InternalJwtConfiguration {

    private String secretKey;
    private int expiredIntervalSeconds;

    public InternalJwtConfiguration(String secretKey, int expiredIntervalSeconds) {
        this.expiredIntervalSeconds = expiredIntervalSeconds;
        this.secretKey = secretKey;
    }

    public InternalJwtConfiguration(String secretKey) {
        this(secretKey, 10);
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public int getExpiredIntervalSeconds() {
        return this.expiredIntervalSeconds;
    }

}
