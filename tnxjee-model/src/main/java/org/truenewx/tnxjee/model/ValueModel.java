package org.truenewx.tnxjee.model;

/**
 * 值模型<br/>
 * 没有标识属性来唯一表示一个实例，实现类需覆写{@link Object#hashCode()}和
 * {@link Object#equals(Object)} ，以便更好地区分不同实例
 *
 * @author jianglei
 * 
 */
public interface ValueModel extends Model {
    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);
}
