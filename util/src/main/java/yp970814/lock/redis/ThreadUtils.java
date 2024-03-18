package yp970814.lock.redis;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-18 14:32
 */
public class ThreadUtils {
    public static Thread getThreadByThreadId(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while (group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for (int i = 0; i < count; i++) {
                if (threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
        }
        return null;
    }
}
