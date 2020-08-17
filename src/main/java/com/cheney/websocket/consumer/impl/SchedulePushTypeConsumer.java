package com.cheney.websocket.consumer.impl;

import com.alibaba.fastjson.JSONObject;
import com.cheney.utils.scan.PathScan;
import com.cheney.utils.scan.ScanException;
import com.cheney.utils.scan.filter.ScanFilter;
import com.cheney.websocket.consumer.TypeConsumer;
import com.cheney.websocket.consumer.impl.schedule.ScheduleConsumer;
import com.cheney.websocket.dto.Message;
import com.cheney.websocket.utils.MessageTypeHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进度信息推送
 *
 * @author cheney
 * @Date 2020/8/07 15:32
 **/
@Slf4j
public class SchedulePushTypeConsumer implements TypeConsumer {

    /**
     * 进度消息消费组
     */
    private Map<String, List<ScheduleConsumer>> scheduleConsumers;

    public SchedulePushTypeConsumer() {
        String packageName = this.getClass().getPackageName();
        registerConsumer(packageName);
    }

    @Override
    @SneakyThrows
    public void consume(Message msg, Session session) {
        log.info("【申请单汇总】请求汇总创建申请单，req:{}", msg);
        MessageTypeHolder.MessageType.SCHEDULE_PUSH.addSessionId(session.getId());
        JSONObject data = msg.getData();
        String type = data.getString("type");
        List<ScheduleConsumer> consumers = scheduleConsumers.get(type);
        if (!CollectionUtils.isEmpty(consumers)) {
            consumers.forEach(c -> c.consume(msg, session));
        }
    }

    private void registerConsumer(String packageName) {
        this.scheduleConsumers = new ConcurrentHashMap<>();
        ScanFilter scanFilter = new ScanFilter();
        scanFilter.setSuperClass(ScheduleConsumer.class);
        try {
            for (Class<?> clazz : new PathScan(scanFilter).scanClass(packageName)) {
                try {
                    ScheduleConsumer scheduleConsumer = (ScheduleConsumer) clazz.getDeclaredConstructor().newInstance();
                    this.scheduleConsumers.computeIfAbsent(scheduleConsumer.type(), k -> new ArrayList<>()).add(scheduleConsumer);
                } catch (Exception e) {
                    log.error("创建消费者实例失败", e);
                }
            }
        } catch (ScanException e) {
            log.error("扫描进度消费组失败", e);
        }
    }
}
