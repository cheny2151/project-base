package com.cheney.controller;

import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import cn.cheny.toolbox.redis.clustertask.pub.ClusterTaskPublisher;
import com.cheney.dao.mybatis.AuthUserMapper;
import com.cheney.entity.AuthUser;
import com.cheney.entity.Role;
import com.cheney.service.RoleService;
import com.cheney.system.response.JsonMessage;
import com.cheney.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;

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

/*    @Resource
    private EntityBufferHolder entityBufferHolder;*/

    @Resource
    private AuthUserMapper authUserMapper;

    @RequestMapping("/test")
    @ResponseBody
    public JsonMessage test() {
        Map<String, Object> header = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        header.put("list", list);
        clusterTaskPublisher.publish("test1", list.size(), 1, 1, true, header);
        return JsonMessage.success("123");
    }

    @RequestMapping("/test2")
    @ResponseBody
    public void test2() {
        Role test = roleService.getByCache("test3");
        System.out.println(test);
    }
/*
    @RequestMapping("/test3")
    @ResponseBody
    public void test3() {
        entityBufferHolder.refreshCache(Role.class);
    }*/

    @RequestMapping("/test4")
    @ResponseBody
    public void test4() {
        Date date = new Date();
        Date date1 = DateUtils.parseDate("2020-10");
        List<AuthUser> test = authUserMapper.test("2020-10", "2020-12");
        System.out.println(test);
    }
}
