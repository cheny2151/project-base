package com.cheney.redis.clustertask.sub;

import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.system.page.Limit;
import lombok.extern.slf4j.Slf4j;

/**
 * 集群任务订阅抽象接口
 *
 * @author cheney
 * @date 2019-09-03
 */
@Slf4j
public abstract class AbstractClusterTaskSubscriber implements ClusterTaskSubscriber {

    @Override
    public void execute(TaskInfo taskInfo, Limit limit) {
        try {
            before(taskInfo);
            subscribe(taskInfo, limit);
            after(taskInfo);
        } catch (Exception e) {
            error(e);
        }
    }

    @Override
    public void before(TaskInfo taskInfo) {
    }

    @Override
    public void after(TaskInfo taskInfo) {
    }

    @Override
    public void error(Throwable t) {
        log.error("订阅器执行集群任务异常", t);
    }

}
