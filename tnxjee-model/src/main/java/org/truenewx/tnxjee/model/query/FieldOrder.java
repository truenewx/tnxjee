package org.truenewx.tnxjee.model.query;

import java.io.Serializable;
import java.util.Objects;

import org.truenewx.tnxjee.core.util.function.FuncHashCode;

/**
 * 字段排序
 *
 * @author jianglei
 */
public class FieldOrder implements Serializable, Cloneable {

    private static final long serialVersionUID = 3974601723760230151L;

    private String name;
    private boolean desc;

    public FieldOrder() {
    }

    public FieldOrder(String name, boolean desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDesc() {
        return this.desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    @Override
    public int hashCode() {
        Object[] array = { this.name, this.desc };
        return FuncHashCode.INSTANCE.apply(array);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FieldOrder other = (FieldOrder) obj;
        return Objects.equals(this.name, other.name) && this.desc == other.desc;
    }

    @Override
    public String toString() {
        return this.name + " " + this.desc;
    }

    @Override
    public FieldOrder clone() {
        return new FieldOrder(this.name, this.desc);
    }

}
