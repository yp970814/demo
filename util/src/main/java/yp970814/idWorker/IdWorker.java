package yp970814.idWorker;

import java.net.InetAddress;

/**
 * 生成全局ID
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:35
 */
public class IdWorker {

    private final long workerId;

    /**
     * 滤波器,使时间变小,生成的总位数变小,一旦确定不能变动
     */
    private long sequence = 0L;
    private final static long TWEPOCH = 1361753741828L;
    private final static long WORKER_ID_BITS = 10L;
    private final static long MAX_WORKER_ID = -1L ^ -1L << WORKER_ID_BITS;
    private final static long SEQUENCE_BITS = 12L;

    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long SEQUENCE_MASK = -1L ^ -1L << SEQUENCE_BITS;

    private long lastTimestamp = -1L;

    /**
     * 根据主机id获取机器码
     */
    private static IdWorker worker = new IdWorker();

    /**
     * 创建 IdWorker对象.
     *
     * @Deprecated 请调用静态方法getId()
     * @param workerId
     */
    @Deprecated
    public IdWorker(final long workerId) {
        if (workerId > IdWorker.MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", IdWorker.MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    public IdWorker() {
        this.workerId = getAddress() % (IdWorker.MAX_WORKER_ID + 1);
    }

    public static long getId() {
        return worker.nextId();
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) {
            this.sequence = (this.sequence + 1) & IdWorker.SEQUENCE_MASK;
            if (this.sequence == 0) {
                // System.out.println("###########" + SEQUENCE_MASK);//等待下一毫秒
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }
        if (timestamp < this.lastTimestamp) {
            try {
                throw new Exception(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                        this.lastTimestamp - timestamp));
            } catch (Exception e) {
            }
        }

        this.lastTimestamp = timestamp;
        long nextId = ((timestamp - TWEPOCH << TIMESTAMP_LEFT_SHIFT)) | (this.workerId << IdWorker.WORKER_ID_SHIFT)
                | (this.sequence);
        // System.out.println("timestamp:" + timestamp + ",TIMESTAMP_LEFT_SHIFT:"
        // + TIMESTAMP_LEFT_SHIFT + ",nextId:" + nextId + ",workerId:"
        // + workerId + ",sequence:" + sequence);
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp1) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp1) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private static long getAddress() {
        try {
            String currentIpAddress = InetAddress.getLocalHost().getHostAddress();
            String[] str = currentIpAddress.split("\\.");
            StringBuilder hardware = new StringBuilder();
            for (int i = 0; i < str.length; i++) {
                hardware.append(str[i]);
            }
            return Long.parseLong(hardware.toString());
        } catch (Exception e) {
        }

        return 2L;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(final String[] args) {
        // IdWorker worker2 = new IdWorker(0);
        // System.out.println(worker2.nextId());
        // long ll = getAddress() % 16;
        // System.out.println(ll);
        long start = System.currentTimeMillis();
        int i100000=100000;
        for (int i = 0; i < i100000; i++) {
            System.out.println(getId());
        }
        long end = System.currentTimeMillis();
        System.out.println((100000 / (end - start)) + "个/ms");
        System.out.println(getId());
    }
}
