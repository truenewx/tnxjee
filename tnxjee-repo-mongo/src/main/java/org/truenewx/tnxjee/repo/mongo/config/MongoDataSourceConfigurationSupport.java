package org.truenewx.tnxjee.repo.mongo.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.truenewx.tnxjee.repo.mongo.support.MongoSchemaTemplate;

import com.mongodb.MongoClientURI;

/**
 * MongoDB数据源配置支持
 *
 * @author jianglei
 */
public abstract class MongoDataSourceConfigurationSupport extends EmbeddedMongoConfiguration {

    private String uri;

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public MongoDbFactory mongoDbFactory() {
        if (StringUtils.isNotBlank(this.uri)) {
            MongoClientURI mongoClientURI = new MongoClientURI(this.uri);
            return new SimpleMongoDbFactory(mongoClientURI);
        } else {
            return super.mongoDbFactory();
        }
    }

    public MongoSchemaTemplate schemaTemplate() throws Exception {
        String schema = getDatabaseName();
        if (schema == null) {
            return new MongoSchemaTemplate(mongoTemplate());
        } else {
            return new MongoSchemaTemplate(schema, mongoTemplate());
        }
    }

}
