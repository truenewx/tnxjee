package org.truenewx.tnxjee.repo.mongo.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.truenewx.tnxjee.repo.mongo.support.MongoAccessTemplate;
import org.truenewx.tnxjee.repo.util.RepoUtil;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

/**
 * MongoDB配置支持
 *
 * @author jianglei
 */
@AutoConfigureBefore(MongoDataAutoConfiguration.class)
public abstract class MongoConfigSupport extends AbstractMongoConfiguration {

    private MongoClientURI uri;

    public void setUri(String uri) {
        this.uri = new MongoClientURI(uri);
    }

    @Override
    protected String getDatabaseName() {
        return this.uri == null ? RepoUtil.DEFAULT_SCHEMA_NAME : this.uri.getDatabase();
    }

    @Override
    public MongoClient mongoClient() {
        if (this.uri != null) {
            return new MongoClient(this.uri);
        } else { // 未指定URI，则使用内嵌的MongoDB，用于单元测试
            return embededMongoClient();
        }
    }

    protected MongoClient embededMongoClient() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return new MongoClient(new ServerAddress(mongoServer.getLocalAddress()));
    }

    public MongoAccessTemplate mongoAccessTemplate() throws Exception {
        String schema = getDatabaseName();
        if (schema == null) {
            return new MongoAccessTemplate(mongoTemplate());
        } else {
            return new MongoAccessTemplate(schema, mongoTemplate());
        }
    }

}
