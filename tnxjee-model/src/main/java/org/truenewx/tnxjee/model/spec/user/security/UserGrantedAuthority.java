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
    private Set<String> permissions;

    public UserGrantedAuthority() {
    }

    public UserGrantedAuthority(String type, String rank) {
        this.type = type;
        this.rank = rank;
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

    @Override
    @JsonIgnore
    public String getAuthority() {
        // 形如： type|rank|permission0,permission1,...
        StringBuilder authority = new StringBuilder();
        StringUtil.ifNotBlank(this.type, (Consumer<String>) authority::append);
        authority.append(Strings.VERTICAL_BAR);
        StringUtil.ifNotBlank(this.rank, (Consumer<String>) authority::append);
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
