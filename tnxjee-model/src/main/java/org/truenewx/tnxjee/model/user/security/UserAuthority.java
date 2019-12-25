package org.truenewx.tnxjee.model.user.security;

import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;

/**
 * 用户权限
 */
public abstract class UserAuthority {

    public static final String SEPARATOR = Strings.PLUS;

    private String role;
    private String permission;

    public UserAuthority(String role, String permission) {
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
    }

    /**
     * 拆解指定字符串以构建实例
     *
     * @param s 用分隔符分隔了角色和许可的字符串
     */
    public UserAuthority(String s) {
        int index = s.indexOf(SEPARATOR);
        Assert.isTrue(index >= 0, () -> "The s must contain '" + SEPARATOR + "'");
        this.role = s.substring(0, index);
        this.permission = s.substring(index + SEPARATOR.length());
    }

    public String getRole() {
        return this.role;
    }

    public String getPermission() {
        return this.permission;
    }

    @Override
    public String toString() {
        return this.role + SEPARATOR + this.permission;
    }

}
