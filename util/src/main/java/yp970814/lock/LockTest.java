package yp970814.lock;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-09-11 15:34
 */
public class LockTest {

    static CreateLock lock = new CreateLock();

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Thread a = new Thread(()->{
            testLock();
            while (true) {

            }
        });
        Thread b = new Thread(()->{
            testLock();
        });
        a.setName("A");
        b.setName("B");
        a.start();
        b.start();
    }

    static void testLock() {
        try {
            System.out.println("获取锁：" + Thread.currentThread().getName());
            lock.lock();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("释放锁：" + Thread.currentThread().getName());
            lock.unlock();
        }
    }

}
