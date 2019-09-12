package org.truenewx.tnxjee.repo.mongo.support;

import org.truenewx.tnxjee.model.definition.Entity;
import org.truenewx.tnxjee.repo.support.RepoSupport;

/**
 * MongoDB数据访问仓库支持
 *
 * @author jianglei
 */
public abstract class MongoRepoSupport<T extends Entity> extends RepoSupport<T> {

    @Override
    protected MongoSchemaTemplate getSchemaTemplate() {
        return (MongoSchemaTemplate) super.getSchemaTemplate();
    }

}
