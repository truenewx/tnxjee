package org.truenewx.tnxjee.model.spec.user;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.truenewx.tnxjee.core.Strings;

/**
 * 用户范围
 */
public class UserScope {

    /**
     * 用户类型
     */
    private String type;
    /**
     * 用户级别
     */
    private String rank;

    public UserScope() {
    }

    public UserScope(String type, String rank) {
        setType(type);
        setRank(rank);
    }

    public static UserScope of(String s) {
        int index = s.indexOf(Strings.DOT);
        if (index < 0) {
            return new UserScope(s, null);
        }
        String type = s.substring(0, index);
        String rank = s.substring(index + 1);
        return new UserScope(type, rank);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        Assert.isTrue(StringUtils.isNotBlank(type), "The type must be not blank");
        this.type = type;
    }

    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserScope userScope = (UserScope) o;
        return Objects.equals(this.type, userScope.type) &&
                Objects.equals(this.rank, userScope.rank);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.rank);
    }

    @Override
    public String toString() {
        return StringUtils.isBlank(this.rank) ? this.type : (this.type + Strings.DOT + this.rank);
    }

}
