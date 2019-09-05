package org.truenewx.tnxjee.repo.jpa;

import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.Repo;

/**
 * JPA的数据访问仓库
 *
 * @author jianglei
 */
public interface JpaRepo<T extends Entity> extends Repo<T> {

    void flush();

    void refresh(T entity);

}
