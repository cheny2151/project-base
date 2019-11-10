package com.cheney.controller;

import com.cheney.entity.BaseEntity;
import com.cheney.entity.Role;
import com.cheney.redis.client.RedisClient;
import com.cheney.redis.clustertask.pub.ClusterTaskPublisher;
import com.cheney.service.RoleService;
import com.cheney.system.response.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller("pageCommonController")
public class PageCommonController {

    @Resource(name = "redisClientWithLog")
    private RedisClient<String> redisClient;

//    @Resource(name = "strKafkaTemplate")
//    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource(name = "defaultClusterTaskPublisher")
    private ClusterTaskPublisher clusterTaskPublisher;

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

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
        Role test = roleService.getByCache("test3");
        System.out.println(test);
    }
}
