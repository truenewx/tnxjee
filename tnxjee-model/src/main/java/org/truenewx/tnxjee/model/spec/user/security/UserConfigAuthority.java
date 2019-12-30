package org.truenewx.tnxjee.model.spec.user.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;

/**
 * 要求用户必须具备的权限
 */
public class UserConfigAuthority implements ConfigAttribute {

    private static final long serialVersionUID = 912979753766969750L;

    public static final String SEPARATOR = Strings.PLUS;

    private String role;
    private String permission;
    /**
     * 是否仅限内网访问
     */
    private boolean intranet;

    public UserConfigAuthority(String role, String permission, boolean intranet) {
        if (role == null) {
            role = Strings.EMPTY;
        }
        Assert.isTrue(!role.contains(SEPARATOR), () -> "The role can not contain '" + SEPARATOR + "'");
        if (permission == null) {
            permission = Strings.EMPTY;
        }
        Assert.isTrue(!permission.contains(SEPARATOR), () -> "The permission can not contain '" + SEPARATOR + "'");
        this.role = role;
        this.permission = permission;
        this.intranet = intranet;
    }

    /**
     * 构建没有权限限制、登录即可访问的必备权限
     */
    public UserConfigAuthority() {
        this(null,null,false);
    }

    public String getRole() {
        return this.role;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isIntranet() {
        return this.intranet;
    }

    @Override
    public String getAttribute() {
        return this.role + SEPARATOR + this.permission;
    }

    @Override
    public String toString() {
        return getAttribute();
    }

}
