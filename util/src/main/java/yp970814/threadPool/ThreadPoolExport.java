package yp970814.threadPool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多线程导出、导入
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 14:44
 */
public class ThreadPoolExport {

    public void export() throws Exception {
        // 起始
        long begin = System.currentTimeMillis();
        Executor threadPool = new ThreadPoolExecutor(15, 15, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(1000));
//		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
//		final ExecutorCompletionService<List> executorCompletionService = new ExecutorCompletionService(threadPool);
        int pagecount = 2000; // 单页数据量
        AtomicInteger page = new AtomicInteger(0); // 分页数
        AtomicInteger count = new AtomicInteger(15); // 并发计数器，控制并发数量
        AtomicInteger executingCount = new AtomicInteger(0); // 执行计数器
        AtomicInteger exportCount = new AtomicInteger(1); // 导出总数量
        AtomicBoolean condition = new AtomicBoolean(true);// 任务状态标志，true：可执行；false：已结束，不可执行
        Lock lock = new ReentrantLock(true);// 公平锁
        final String csvUrl = "C:\\Users\\yuanping\\Desktop\\export.csv";
        File file = new File(csvUrl);
        if (file.exists()) file.delete();
        while (condition.get()) {
            // 计数器 >0，才能向下执行，否则等待
            if (count.get() < 1) {
                Thread.currentThread().sleep(100);
                continue;
            }
            // 并发计数器 -1
            count.decrementAndGet();
            // 执行计数器 +1
            executingCount.incrementAndGet();
//			System.out.println("队列count.get()：" + count.get());
//			System.out.println("队列中等待任务：" + ((ThreadPoolExecutor)threadPool).getQueue().size());
            threadPool.execute(() -> {
                // 任务已结束，线程池仍在执行
                if (!condition.get()) {
                    count.incrementAndGet();
                    return;
                }
                String sql =
                    "select b.* " +
                    "  from (select rownum as rn, a.* " +
                    "				from (select salhb_id " +
                    "						from nywms1.tb_salhb order by salhb_id desc) a " +
                    "		  where rownum <= " + (page.get() * pagecount + pagecount) + ") b " +
                    " where b.rn > " + (page.get() * pagecount);
                page.incrementAndGet();
                List<String> list = this.findBySql(sql);
                if (list == null || list.size() == 0) {
                    while (!condition.compareAndSet(condition.get(), false));
                    count.incrementAndGet();
                    return;
                }
                // 按序号进行
                while (Integer.valueOf(list.get(0).substring(list.get(0).indexOf("\"") + 1, list.get(0).indexOf("\","))).compareTo(exportCount.get()) != 0) {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                while (!exportCount.compareAndSet(exportCount.get(), exportCount.get() + list.size()));
                lock.lock();
                try {
                    exportCsv(list, csvUrl);
                    count.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
            Thread.currentThread().sleep(10);
        }
        while (condition.get() || count.get() < 15) {
            Thread.currentThread().sleep(100);
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - begin));
    }

    public List<String> findBySql(String sql) {
        return null;
    }

    public static String exportCsv(List<String> list, String url) throws Exception {
        File file = new File(url);
        //构建输出流，同时指定编码
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file, true), "gbk");
        //写内容
        for (String obj : list) {
            ow.write(obj);
            //写完一行换行
            ow.write("\r\n");
        }
        ow.flush();
        ow.close();
        return "0";
    }

}
