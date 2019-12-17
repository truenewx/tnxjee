package org.truenewx.tnxjee.core.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.spring.core.env.ProfileSupplier;

/**
 * 抽象的版本号读取器
 *
 * @author jianglei
 */
public abstract class AbstractVersionReader implements VersionReader, ApplicationContextAware {

    @Autowired
    private ProfileSupplier profileSupplier;
    private String base;
    private String build = "0";

    @Override
    public void setApplicationContext(ApplicationContext context) {
        String version = readFullVersion(context);
        if (version != null) {
            int baseLevel = 3;
            String[] fragments = version.split("\\.", baseLevel + 1);
            this.base = getVersion(fragments, baseLevel);
            if (fragments.length > baseLevel) {
                this.build = fragments[baseLevel];
            }
        }
    }

    protected String getVersion(Object[] fragments, int level) {
        StringBuffer base = new StringBuffer();
        for (int i = 0; i < level; i++) {
            if (fragments.length > i) {
                base.append(fragments[i]);
            } else {
                base.append(0); // 不足的用0代替
            }
            base.append(Strings.DOT);
        }
        if (base.length() > 0) { // 去掉末尾的句点
            base.deleteCharAt(base.length() - 1);
        }
        return base.toString();
    }

    /**
     * 读取版本号数组
     *
     * @param context Spring容器上下文
     * @return 版本号数组
     * @author jianglei
     */
    protected abstract String readFullVersion(ApplicationContext context);

    @Override
    public String getVersion(boolean withBuild) {
        if (this.base == null) {
            return null;
        }
        if (withBuild) {
            return this.base + Strings.DOT + this.build;
        } else {
            return this.base;
        }
    }

    @Override
    public String getVersion() {
        boolean withBuild = !this.profileSupplier.isFormal();
        return getVersion(withBuild);
    }

    @Override
    public String getBuild() {
        return this.build;
    }

}
