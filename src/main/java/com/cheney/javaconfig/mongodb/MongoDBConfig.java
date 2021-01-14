package com.cheney.javaconfig.mongodb;

import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

//@Configuration
@EnableMongoRepositories(basePackages = {"com.cheney"})
public class MongoDBConfig {

    private final Environment env;

    @Autowired
    public MongoDBConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public MongoClientFactoryBean mongo() {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        if (env.getProperty("mongodb.auth", boolean.class, false)) {
            //此处认证的database是指use的所属的数据库
            MongoCredential credential = MongoCredential.createCredential(env.getRequiredProperty("mongodb.username"),
                    env.getRequiredProperty("mongodb.authBase"), env.getRequiredProperty("mongodb.password").toCharArray());
            mongo.setCredential(new MongoCredential[]{credential});
        }
        mongo.setHost(env.getProperty("mongodb.host", "localhost"));
        return mongo;
    }

    @Bean
    public MongoOperations mongoTemplate(MongoClient mongo) {
        return new MongoTemplate(mongo, env.getProperty("mongodb.database"));
    }

}
