package com.cheney.redis.clustertask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author cheney
 * @date 2019-09-03
 */
public class TaskInfo extends HashMap<String, String> {

    public TaskInfo(String taskId, int dataNums, int StepCount, int StepSize) {
        super(8);
        setStepCount(StepCount);
        setDataNums(dataNums);
        setTaskId(taskId);
        setStepSize(StepSize);
    }

    public TaskInfo(Map<String, String> params) {
        super(params);
    }

    /**
     * 总数居量
     */
    public Integer getDataNums() {
        return Integer.parseInt(get("dataNums"));
    }

    public void setDataNums(int dataNums) {
        put("dataNums", String.valueOf(dataNums));
    }

    /**
     * 步数
     */
    public Integer getStepCount() {
        return Integer.parseInt(get("stepCount"));
    }

    public void setStepCount(int StepCount) {
        put("stepCount", String.valueOf(StepCount));
    }

    /**
     * 任务ID
     */
    public String getTaskId() {
        return (String) get("taskId");
    }

    public void setTaskId(String taskId) {
        put("taskId", taskId);
    }

    /**
     * 步长
     */
    public Integer getStepSize() {
        return Integer.parseInt(get("stepSize"));
    }

    public void setStepSize(int StepSize) {
        put("stepSize", String.valueOf(StepSize));
    }

    public boolean isValid() {
        return getTaskId() != null && getStepCount() != null
                && getStepSize() != null && getStepCount() != null;
    }
}
