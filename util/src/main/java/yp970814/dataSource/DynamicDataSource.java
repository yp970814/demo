package yp970814.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import yp970814.threadLocal.ThreadLocalUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-03-13 10:50
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSource.class);

    private AtomicInteger runCount = new AtomicInteger(-1);

    //返回分配的数据库的key
    @Override
    protected Object determineCurrentLookupKey() {
        //Service的方法中明确标注@Transactional，会先执行com.dy.config.DynamicDataSourceTransactionManager.doBegin()，
        //根据@Transactional的参数是否是标记为readonly返回主库还是从库
        //如果Service的方法未标注@Transactional，则无法从事务判断，全部从从库读取
        //从库使用依次分配的方式均衡负载
        String dataSourceName = DynamicDataSourceGlobal.WRITE.name();
        DynamicDataSourceGlobal dynamicDataSourceGlobal = (DynamicDataSourceGlobal) ThreadLocalUtil.getValue("DataSource");
        if (dynamicDataSourceGlobal == DynamicDataSourceGlobal.WRITE) {
            dataSourceName = DynamicDataSourceGlobal.WRITE.name();
        } else if (dynamicDataSourceGlobal == DynamicDataSourceGlobal.READ1) {
            dataSourceName = DynamicDataSourceGlobal.READ1.name();
        } else if (dynamicDataSourceGlobal == DynamicDataSourceGlobal.READ2) {
            dataSourceName = DynamicDataSourceGlobal.READ2.name();
        } else  {
            if (runCount.getAndIncrement() % 2 == 0) {
                dataSourceName = DynamicDataSourceGlobal.READ1.name();
            } else {
                dataSourceName = DynamicDataSourceGlobal.READ2.name();
            }
        }
        dataSourceName = DynamicDataSourceGlobal.WRITE.name();
        LOGGER.debug("切换:DynamicDataSource:determineCurrentLookupKey:" + dataSourceName);
        return dataSourceName;
    }

}
