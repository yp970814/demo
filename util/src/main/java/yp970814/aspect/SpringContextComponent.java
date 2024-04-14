package yp970814.aspect;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.ReferenceBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import yp970814.properties.SysProperties;

import java.util.Properties;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 16:36
 */
@Component
public class SpringContextComponent implements ApplicationContextAware {

    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return getApplicationContext() == null ? null : getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext() == null ? null : getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext() == null ? null : getApplicationContext().getBean(name, clazz);
    }

    public static <T> T getDubboBean(Class<T> clazz) throws Exception {
        Properties prop = SysProperties.INSTANCE.getProperties("dubbo.properties");
        ReferenceBean<T> ref = new ReferenceBean<T>();
        ref.setVersion("1.0");
        ref.setInterface(clazz);
        ref.setCheck(false);
        ref.setTimeout(30000);
        ref.setGroup(clazz.getSimpleName());
        ref.setApplication(new ApplicationConfig(prop.getProperty("dubbo.appName")));
        ref.setRegistry(new RegistryConfig(prop.getProperty("dubbo.registry.address")));
        ref.afterPropertiesSet();
        return ref.get();
    }

    public static <T> T getDubboBean(Class<T> clazz, String version) throws Exception {
        Properties prop = SysProperties.INSTANCE.getProperties("dubbo.properties");
        ReferenceBean<T> ref = new ReferenceBean<T>();
        ref.setVersion(version);
        ref.setInterface(clazz);
        ref.setCheck(false);
        ref.setTimeout(30000);
        ref.setGroup(clazz.getSimpleName());
        ref.setApplication(new ApplicationConfig(prop.getProperty("dubbo.appName")));
        ref.setRegistry(new RegistryConfig(prop.getProperty("dubbo.registry.address")));
        ref.afterPropertiesSet();
        return ref.get();
    }

}
