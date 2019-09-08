package com.cheney.controller;

import com.cheney.dao.mybatis.UserMapper;
import com.cheney.redis.client.RedisClient;
import com.cheney.redis.clustertask.pub.ClusterTaskPublisher;
import com.cheney.system.message.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller("pageCommonController")
public class PageCommonController {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "redisClientWithLog")
    private RedisClient<String> redisClient;

    @Resource(name = "strKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource(name = "defaultClusterTaskPublisher")
    private ClusterTaskPublisher clusterTaskPublisher;

    @Autowired
    private Environment environment;

    @RequestMapping("/test")
    @ResponseBody
    public JsonMessage test() {
        clusterTaskPublisher.publish("test", 999, 100, 4, true);
        return JsonMessage.success("123");
    }

    @RequestMapping("/test2")
    @ResponseBody
    public void test2() {
        kafkaTemplate.send("test", "template send msg");
    }
}
