package org.truenewx.tnxjee.model.spec.user.security;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.StringUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 用户已获权限
 */
public class UserGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -2834509708888634914L;

    private String type;
    private String rank;
    private String app;
    private Set<String> permissions;

    public UserGrantedAuthority() {
    }

    public UserGrantedAuthority(String type, String rank, String app) {
        this.type = type;
        this.rank = rank;
        this.app = app;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRank() {
        return this.rank;
    }

    public String getApp() {
        return this.app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    //////

    public void addPermission(String permission) {
        if (StringUtils.isNotBlank(permission)) {
            if (this.permissions == null) {
                this.permissions = new LinkedHashSet<>();
            }
            this.permissions.add(permission);
        }
    }

    public void addPermissions(Collection<String> permissions) {
        if (CollectionUtils.isNotEmpty(permissions)) {
            if (this.permissions == null) {
                this.permissions = new LinkedHashSet<>();
            }
            this.permissions.addAll(permissions);
        }
    }

    public boolean matches(String type, String rank, String app, String permission) {
        if (StringUtils.isNotBlank(type) && !type.equals(this.type)) { // 限定了用户类型但不相等，则不匹配
            return false;
        }
        if (StringUtils.isNotBlank(rank) && !rank.equals(this.rank)) { // 限定了用户级别但不相等，则不匹配
            return false;
        }
        if (StringUtils.isNotBlank(app) && !app.equals(this.app)) { // 限定了应用但不相等，则不匹配
            return false;
        }
        // 限定了许可但未包含，则不匹配
        if (StringUtils.isNotBlank(permission) && !StringUtil.wildcardMatchOneOf(permission, this.permissions)) {
            return false;
        }
        return true;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        // 形如： type|rank|permission0,permission1,...
        StringBuilder authority = new StringBuilder();
        StringUtil.ifNotBlank(this.type, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        StringUtil.ifNotBlank(this.rank, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        StringUtil.ifNotBlank(this.app, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        if (this.permissions != null) {
            authority.append(StringUtils.join(this.permissions, Strings.COMMA));
        }
        return authority.toString();
    }

    @Override
    public String toString() {
        return getAuthority();
    }

}
