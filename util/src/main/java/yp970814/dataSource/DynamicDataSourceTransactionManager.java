package yp970814.dataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import yp970814.threadLocal.ThreadLocalUtil;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-03-13 10:51
 */
public class DynamicDataSourceTransactionManager extends DataSourceTransactionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceTransactionManager.class);

    private AtomicInteger runCount = new AtomicInteger(-1);

    /**
     * 获取数据库连接前调用
     * 只读事务到读库，读写事务到写库
     * @param transaction
     * @param definition
     */
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        //设置数据源
        boolean readOnly = definition.isReadOnly();
        if(readOnly) {
            //从库 读 简单负载均衡
            int index = runCount.getAndIncrement() % 2;
            if (index == 0) {
                ThreadLocalUtil.setValue("DataSource", DynamicDataSourceGlobal.READ1);
            } else {
                ThreadLocalUtil.setValue("DataSource", DynamicDataSourceGlobal.READ2);
            }
        } else {
            ThreadLocalUtil.setValue("DataSource", DynamicDataSourceGlobal.WRITE);
        }
        super.doBegin(transaction, definition);
    }

    //清理线程共享的数据源名称
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        ThreadLocalUtil.remove("DataSource");
    }

}
