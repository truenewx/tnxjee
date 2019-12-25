package org.truenewx.tnxjee.model.user;

/**
 * 整数型用户标识
 *
 * @author jianglei
 */
public class IntegerUserIdentity implements UserIdentity {

    private static final long serialVersionUID = -1388580489232151252L;

    private Integer value;

    public IntegerUserIdentity(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
