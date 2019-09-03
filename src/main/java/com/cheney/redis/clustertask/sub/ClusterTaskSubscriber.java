package com.cheney.redis.clustertask.sub;

import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.system.page.Limit;

/**
 * 集群任务订阅
 *
 * @author cheney
 * @date 2019-09-03
 */
public interface ClusterTaskSubscriber {

    void subscribe(TaskInfo taskInfo, Limit limit);

    void execute(TaskInfo taskInfo, Limit limit);

    void before(TaskInfo taskInfo);

    void after(TaskInfo taskInfo);

    void error(Throwable t);

}
