package org.truenewx.tnxjee.core.spring.core.env;

import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.beans.ContextInitializedBean;
import org.truenewx.tnxjee.core.util.CorePropertyConstant;

/**
 * 函数：获取当前profile
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Component
public class ProfileSupplier implements Supplier<String>, ApplicationContextAware {

    private String profile = Strings.EMPTY; // 默认为空，表示无profile区分
    /**
     * 是否正式环境
     */
    private boolean formal;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        String[] profiles = context.getEnvironment().getActiveProfiles();
        if (profiles.length > 0) {
            this.profile = profiles[0];
            String formal = context.getEnvironment().getProperty(CorePropertyConstant.PROFILE_FORMAL, "false");
            this.formal = Boolean.valueOf(formal);
        }
    }

    @Override
    public String get() {
        return this.profile;
    }

    public boolean isFormal() {
        return this.formal;
    }
}
