package org.truenewx.tnxjee.model.spec.user;

import java.io.Serializable;

/**
 * 用户标识，用于唯一确定整个系统中的一个用户，也可以表示系统本身
 *
 * @author jianglei
 */
public interface UserIdentity<K extends Serializable> extends Serializable {

    default String getType() {
        return null;
    }

    K getId();

}
