package com.cheney.controller;

import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import cn.cheny.toolbox.redis.clustertask.pub.ClusterTaskPublisher;
import com.alibaba.fastjson.JSON;
import com.cheney.entity.AuthUser;
import com.cheney.entity.Role;
import com.cheney.service.RoleService;
import com.cheney.system.response.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller("pageCommonController")
public class PageCommonController {

//    @Resource(name = "strKafkaTemplate")
//    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource(name = "clusterTaskPublisher")
    private ClusterTaskPublisher clusterTaskPublisher;

    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @Resource
    private JsonRedisClient<AuthUser> jsonRedisClient;

    @Autowired
    private Environment environment;

    @RequestMapping("/test")
    @ResponseBody
    public JsonMessage test() {
        AuthUser authUser = jsonRedisClient.getValue("authUser");
        System.out.println(JSON.toJSONString(authUser));
        Map<String, Object> header = new HashMap<>();
        header.put("test", "this is a header info");
        header.put("size", 999);
        clusterTaskPublisher.publish("test1", 999, 100, 4, true, header);
        return JsonMessage.success("123");
    }

    @RequestMapping("/test2")
    @ResponseBody
    public void test2() {
        Role test = roleService.getByCache("test3");
        System.out.println(test);
    }
}
