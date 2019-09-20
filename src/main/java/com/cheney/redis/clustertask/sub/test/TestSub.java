package com.cheney.redis.clustertask.sub.test;

import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.redis.clustertask.pub.ClusterTaskPublisher;
import com.cheney.redis.clustertask.sub.AbstractClusterTaskSubscriber;
import com.cheney.redis.clustertask.sub.SubTask;
import com.cheney.system.page.Limit;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 集群任务调度测试类
 * 继承AbstractClusterTaskSubscriber并存放于spring容器中
 * 再通过@SubTask指定执行的任务ID
 * <p>
 * (任务推送，调用{@link ClusterTaskPublisher#publish(String, int, int, int, boolean)})
 *
 * @author cheney
 * @date 2019-09-03
 */
@Component
@SubTask(taskId = "test")
public class TestSub extends AbstractClusterTaskSubscriber {

    @Override
    public void subscribe(TaskInfo taskInfo, Limit limit) {
        System.out.println("----------------------subscribe test----------------------");
        Calendar instance = Calendar.getInstance();
        int i = instance.get(Calendar.MINUTE);
        if (i == 40) {
            System.out.println("====================invoke stop method=====================");
            stop();
        }
    }

    @Override
    public void afterAllTask(boolean isMaster) {
        System.out.println("=======================finish task=======================");
        if (isMaster) {
            System.out.println("=======================I am master=======================");
        }
    }

}
