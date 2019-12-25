package org.truenewx.tnxjee.model.user.security;

import org.springframework.security.access.ConfigAttribute;

/**
 * 要求用户必须具备的权限
 */
public class UserConfigAuthority extends UserAuthority implements ConfigAttribute {

    private static final long serialVersionUID = 912979753766969750L;

    /**
     * 是否匿名即可访问
     */
    private boolean anonymous;

    /**
     * 是否仅限内网访问
     */
    private boolean intranet;

    public UserConfigAuthority(String role, String permission, boolean intranet) {
        super(role, permission);
        this.intranet = intranet;
    }

    /**
     * 构建表示匿名即可访问的必备权限
     */
    public static UserConfigAuthority ofAnonymous(boolean intranet) {
        UserConfigAuthority configAuthority = new UserConfigAuthority(null, null, intranet);
        configAuthority.anonymous = true;
        return configAuthority;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public boolean isIntranet() {
        return this.intranet;
    }

    @Override
    public String getAttribute() {
        return toString();
    }

    @Override
    public String toString() {
        if (isAnonymous()) {
            return "anonymous"; // 匿名权限字符串中不包含分隔符
        }
        return super.toString();
    }
}
