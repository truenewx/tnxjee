package org.truenewx.tnxjee.model.spec.user;

import java.util.Objects;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;

public class DefaultUserIdentity implements IntegerUserIdentity {

    private static final long serialVersionUID = -1645870192351832325L;

    private String type;
    private String rank;
    private Integer id;

    public DefaultUserIdentity() {
    }

    public DefaultUserIdentity(String type, String rank, Integer id) {
        this.type = type;
        this.rank = rank;
        this.id = id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getRank() {
        return this.rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
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
        DefaultUserIdentity that = (DefaultUserIdentity) o;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.rank, that.rank) &&
                Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.rank, this.id);
    }

    @Override
    public String toString() {
        String type = this.type == null ? Strings.EMPTY : this.type;
        String rank = this.rank == null ? Strings.EMPTY : this.rank;
        String id = this.id == null ? Strings.EMPTY : this.id.toString();
        // 格式：type.rank:id
        return type + Strings.DOT + rank + Strings.COLON + id;
    }

    public static DefaultUserIdentity of(String s) {
        int index = s.indexOf(Strings.DOT);
        if (index < 1) {
            return null;
        }
        String type = s.substring(0, index);
        s = s.substring(index + 1);
        index = s.indexOf(Strings.COLON);
        if (index < 1) {
            return null;
        }
        String rank = s.substring(0, index);
        Integer id = MathUtil.parseInteger(s.substring(index + 1));
        return new DefaultUserIdentity(type, rank, id);
    }

}
