package yp970814.properties;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:25
 */
public enum SysProperties {

    INSTANCE;

    private SysProperties() {

    }

    public static final ThreadLocal<Map<String,String>> serviceMapping = new ThreadLocal<Map<String,String>>();

    private static Logger logger = LoggerFactory.getLogger(SysProperties.class);

//	public static Map<String,String> serviceMapping = new HashMap<String,String>();

    public Properties getProperties(String fileName) {
        Properties properties = null;
        try {
            InputStreamReader propertyIn = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(fileName), "UTF-8");
            properties = new Properties();
            properties.load(propertyIn);
        } catch (IOException e) {
            logger.error(e+"");
        }
        return properties;
    }

    public Properties getSysProperties() {
        Properties properties = null;
        try {
            InputStreamReader propertyIn = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("sysconfig.properties"), "UTF-8");
            properties = new Properties();
            properties.load(propertyIn);
        } catch (IOException e) {
            logger.error(e+"");
        }
        return properties;
    }

    public Properties getOSSProperties() {
        Properties properties = null;
        try {
            InputStreamReader propertyIn = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("oss.properties"), "UTF-8");
            properties = new Properties();
            properties.load(propertyIn);
        } catch (IOException e) {
            logger.error(e+"");
        }
        return properties;
    }

}
