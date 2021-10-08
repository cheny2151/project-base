package com.cheney.controller;

import cn.cheny.toolbox.entityCache.holder.EntityBufferHolder;
import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import cn.cheny.toolbox.redis.clustertask.pub.ClusterTaskPublisher;
import com.cheney.entity.AuthUser;
import com.cheney.entity.Role;
import com.cheney.service.RoleService;
import com.cheney.system.protocol.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Resource
    private EntityBufferHolder entityBufferHolder;

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
//        Role test = roleService.getByCache("test3");
//        System.out.println(test);
    }

    @RequestMapping("/test3")
    @ResponseBody
    public void test3() {
        entityBufferHolder.refreshCache(Role.class);
    }


    /*public static void main(String[] args) throws InterruptedException {
        WebClient.create().get().uri("https://www.baidu.com").headers(h ->{
            System.out.println("header thread:"+Thread.currentThread().getName());
        }).exchangeToMono(resp ->{
            System.out.println("exchange thread:"+Thread.currentThread().getName());
            return resp.toEntity(String.class);
        }).subscribe(e -> {
            System.out.println("subscribe thread:"+Thread.currentThread().getName());
            System.out.println(e);
        });

        Thread.sleep(1000);
    }*/

    public static void main(String[] args) {
        Flux.just("test1", "test2").concatMap(e -> {
            System.out.println("test");
            return Mono.just(e);
        }).next().subscribe(e -> System.out.println(e));
    }
}
