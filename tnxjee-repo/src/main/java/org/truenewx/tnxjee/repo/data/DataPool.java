package org.truenewx.tnxjee.repo.data;

import java.util.List;

import org.truenewx.tnxjee.model.core.Entity;

/**
 * 
 * @author jianglei
 */
public interface DataPool {

    <T extends Entity> List<T> getDataList(Class<T> entityClass);

}