package yp970814.apollo;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-20 21:41
 */
@Slf4j
public class ApolloConfig {

    private Config config = ConfigService.getConfig("application");

    private Map<String, String> configMap;

    @PostConstruct
    public void loadAllConfig() {
        configMap = Maps.newHashMap();
        loadAllApolloConfig();
        log.info("apollo load  application finish");
    }

    private void loadAllApolloConfig() {
        Set<String> keys = config.getPropertyNames();
        keys.stream().forEach(key -> put(key, config.getProperty(key, "")));
    }

    private void put(String key, String value) {
        Assert.isTrue(!configMap.containsKey(key), "key:" + key + "重复");
        configMap.put(key, value);
    }

    private Map<String, String> getAllConfig() {
        return configMap;
    }

    public String getConfigByKey(String key) {
        return configMap.get(key);
    }

    public String getConfigByKey(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    @ApolloConfigChangeListener("application")
    public void onChange(ConfigChangeEvent changeEvent) {
        log.info("apollo load application namespace fresh");
        //监听配置中心
        changeEvent.changedKeys().stream().forEach(key -> {
            ConfigChange change = changeEvent.getChange(key);
            log.info("【apollo】 change {} config from {} to {}", key, change.getOldValue(), change.getNewValue());
        });
        loadAllConfig();
    }

}
