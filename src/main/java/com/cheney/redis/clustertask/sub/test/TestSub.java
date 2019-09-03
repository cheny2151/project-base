package com.cheney.redis.clustertask.sub.test;

import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.redis.clustertask.pub.DefaultClusterTaskPublisher;
import com.cheney.redis.clustertask.sub.AbstractClusterTaskSubscriber;
import com.cheney.redis.clustertask.sub.SubTask;
import com.cheney.system.page.Limit;
import org.springframework.stereotype.Component;

/**
 * 集群任务调度测试类
 * 继承AbstractClusterTaskSubscriber并存放于spring容器中
 * 再通过@SubTask指定执行的任务ID
 * <p>
 * (任务推送，调用{@link DefaultClusterTaskPublisher#publish(String, int, int, int)})
 *
 * @author cheney
 * @date 2019-09-03
 */
@Component
@SubTask(taskId = "test")
public class TestSub extends AbstractClusterTaskSubscriber {

    @Override
    public void subscribe(TaskInfo taskInfo, Limit limit) {

    }

}
