package org.truenewx.tnxjee.core.model;

/**
 * 命名支持
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class NamedSupport implements Named {
    private String name;

    protected NamedSupport(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

}
