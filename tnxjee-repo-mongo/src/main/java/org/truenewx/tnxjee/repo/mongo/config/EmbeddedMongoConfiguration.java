package org.truenewx.tnxjee.repo.mongo.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.truenewx.tnxjee.repo.util.RepoUtil;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

/**
 * 内嵌的MongoDB配置
 *
 * @author jianglei
 */
@AutoConfigureBefore(MongoDataAutoConfiguration.class)
public class EmbeddedMongoConfiguration extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return RepoUtil.DEFAULT_SCHEMA_NAME;
    }

    @Override
    public MongoClient mongoClient() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return new MongoClient(new ServerAddress(mongoServer.getLocalAddress()));
    }

}
