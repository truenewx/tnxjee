package org.truenewx.tnxjee.repo;

import org.springframework.data.repository.Repository;
import org.truenewx.tnxjee.model.entity.Entity;

/**
 * 数据访问仓库<br/>
 * 用于定义在spring-data的{@link Repository}标准规范方法之外的扩展的数据访问方法<br/>
 * 一个实体类型必须要有对应的{@link Repository}，但可以没有对应的{@link Repo}
 *
 * @author jianglei
 * @param <T> 实体类型
 */
public interface Repo<T extends Entity> {

}
