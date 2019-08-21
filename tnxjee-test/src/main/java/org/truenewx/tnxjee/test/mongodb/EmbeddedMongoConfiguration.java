package org.truenewx.tnxjee.test.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

/**
 * 内嵌的MongoDB配置
 *
 * @author jianglei
 */
@TestConfiguration
@AutoConfigureBefore(MongoDataAutoConfiguration.class)
public class EmbeddedMongoConfiguration extends AbstractMongoConfiguration {

    @Autowired
    private Environment env;

    @Override
    protected String getDatabaseName() {
        return this.env.getProperty("spring.data.mongodb.database", "test");
    }

    @Override
    public MongoClient mongoClient() {
        MongoServer mongoServer = new MongoServer(new MemoryBackend());
        mongoServer.bind();
        return new MongoClient(new ServerAddress(mongoServer.getLocalAddress()));
    }

}
