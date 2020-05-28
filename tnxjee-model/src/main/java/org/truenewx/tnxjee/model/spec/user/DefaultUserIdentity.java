package org.truenewx.tnxjee.model.spec.user;

import java.util.Objects;

import org.truenewx.tnxjee.core.Strings;
import org.truenewx.tnxjee.core.util.MathUtil;

public class DefaultUserIdentity implements IntegerUserIdentity {

    private static final long serialVersionUID = -1645870192351832325L;

    private String type;
    private Integer id;

    public DefaultUserIdentity() {
    }

    public DefaultUserIdentity(String type, Integer id) {
        this.type = type;
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
        return Objects.equals(this.type, that.type) && Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.id);
    }

    @Override
    public String toString() {
        String type = this.type == null ? Strings.EMPTY : this.type;
        String id = this.id == null ? Strings.EMPTY : this.id.toString();
        // 格式：type:id
        return type + Strings.COLON + id;
    }

    public static DefaultUserIdentity of(String s) {
        int index = s.indexOf(Strings.COLON);
        if (index < 1) {
            return null;
        }
        String type = s.substring(0, index);
        Integer id = MathUtil.parseInteger(s.substring(index + 1));
        return new DefaultUserIdentity(type, id);
    }

}
