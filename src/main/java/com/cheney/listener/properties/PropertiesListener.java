package com.cheney.listener.properties;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.stereotype.Component;

/**
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

    /*private RefreshScope refreshScope;

    @Autowired
    public PropertiesListener(RefreshScope refreshScope) {
        this.refreshScope = refreshScope;
    }
*/
    @ApolloConfigChangeListener
    public void onchange(ConfigChangeEvent event) {
        for (String key : event.changedKeys()) {
            System.out.println("改变的key:" + key);
            ConfigChange change = event.getChange(key);
            System.out.println("oldVal:" + change.getOldValue());
            System.out.println("newVal:" + change.getNewValue());
        }
        //刷新指定的配置，入参为配置的bean的name
//        refreshScope.refresh()
//        refreshScope.refreshAll();
    }

}
