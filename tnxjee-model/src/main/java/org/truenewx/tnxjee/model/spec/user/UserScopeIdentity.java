package org.truenewx.tnxjee.model.spec.user;

import java.util.Objects;

import org.truenewx.tnxjee.core.Strings;

/**
 * 基于范围的用户标识
 */
public class UserScopeIdentity extends UserScope implements UserIdentity {

    private static final long serialVersionUID = 4706940554029385393L;

    private int id;

    public UserScopeIdentity() {
    }

    public UserScopeIdentity(String type, String rank, int id) {
        super(type, rank);
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserScopeIdentity that = (UserScopeIdentity) o;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.id);
    }

    @Override
    public String toString() {
        return super.toString() + Strings.COLON + this.id;
    }

}
