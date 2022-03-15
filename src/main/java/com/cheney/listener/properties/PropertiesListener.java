package com.cheney.listener.properties;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 配置监听器
 *
 * 另外Apollo jvm参数：
 * -Dapollo.cluster=${cluster}
 * -Dapollo.meta=http://xxx
 * -Denv=${env}
 * -Dapp.id=${appId}
 *
 * @author cheney
 */
@Component
public class PropertiesListener {

    private RefreshScope refreshScope;

    @Autowired
    public PropertiesListener(RefreshScope refreshScope) {
        this.refreshScope = refreshScope;
    }

    /**
     * {@link ApolloConfigChangeListener} 需要指定namespace，默认为application
     */
    @ApolloConfigChangeListener
    public void onchange(ConfigChangeEvent event) {
        for (String key : event.changedKeys()) {
            System.out.println("改变的key:" + key);
            ConfigChange change = event.getChange(key);
            System.out.println("oldVal:" + change.getOldValue());
            System.out.println("newVal:" + change.getNewValue());
        }
        //刷新指定的配置，入参为配置的bean的name,该bean需要加@RefreshScope注解
        refreshScope.refresh("exampleProperties");
//        refreshScope.refreshAll();
    }

}
