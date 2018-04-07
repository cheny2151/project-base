package com.cheney.javaconfig.mongodb;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
//@EnableMongoRepositories(basePackages = {"com.cheney"})
public class MongoDBConfig {

    private final Environment env;

    @Autowired
    public MongoDBConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public MongoClientFactoryBean mongo() {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
//        MongoCredential credential = MongoCredential.createCredential("", "", new char[]{});
//        mongo.setCredentials(new MongoCredential[]{credential});
        mongo.setHost(env.getProperty("mongodb.host"));
        return mongo;
    }

    @Bean
    public MongoOperations mongoTemplate(MongoClient mongo) {
        return new MongoTemplate(mongo, env.getProperty("mongodb.database"));
    }

}
