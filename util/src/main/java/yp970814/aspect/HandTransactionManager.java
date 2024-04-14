package yp970814.aspect;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:22
 */
public class HandTransactionManager {

    public static  synchronized void handRollback(){
        DataSourceTransactionManager transactionManager = (DataSourceTransactionManager)SpringContextComponent.getBean("transactionManager");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); // 事物隔离级别，开启新事务
        TransactionStatus status = transactionManager.getTransaction(def);
        transactionManager.rollback(status);
    }

}
