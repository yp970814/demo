package yp970814.idgenerator;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:58
 */
public class BasicEntityIdGenerator implements EntityIdGenerator {

    private final long sequenceBits = 12L;
    private final long datacenterIdBits = 10L;
    private final long maxDatacenterId = 1023L;
    private final long datacenterIdShift = 12L;
    private final long timestampLeftShift = 22L;
    private final long twepoch = 1288834974657L;
    private final long datacenterId;
    private final long sequenceMax = 4096L;
    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;
    private static BasicEntityIdGenerator basicEntityIdGenerator;

    public static BasicEntityIdGenerator getInstance() throws GetHardwareIdFailedException {
        if (basicEntityIdGenerator == null) {
            basicEntityIdGenerator = new BasicEntityIdGenerator((long)((new Random()).nextInt(1022) + 1));
        }
        return basicEntityIdGenerator;
    }

    public BasicEntityIdGenerator(long datacenterId) throws GetHardwareIdFailedException {
        this.datacenterId = datacenterId;
        if (datacenterId > 1023L || datacenterId < 0L) {
            throw new GetHardwareIdFailedException("datacenterId > maxDatacenterId");
        }
    }

    private BasicEntityIdGenerator() throws GetHardwareIdFailedException {
        this.datacenterId = this.getDatacenterId();
        if (this.datacenterId > 1023L || this.datacenterId < 0L) {
            throw new GetHardwareIdFailedException("datacenterId > maxDatacenterId");
        }
    }

    @Override
    public synchronized Long generateLongId() throws InvalidSystemClockException {
        long timestamp = System.currentTimeMillis();
        if (timestamp < this.lastTimestamp) {
            throw new InvalidSystemClockException("Clock moved backwards.  Refusing to generate id for " + (this.lastTimestamp - timestamp) + " milliseconds.");
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = (this.sequence + 1L) % 4096L;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }
            this.lastTimestamp = timestamp;
            Long id = timestamp - 1288834974657L << 22 | this.datacenterId << 12 | this.sequence;
            return id;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = System.currentTimeMillis(); timestamp <= lastTimestamp; timestamp = System.currentTimeMillis()) {
        }
        return timestamp;
    }

    protected long getDatacenterId() throws GetHardwareIdFailedException {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            long id;
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = (255L & (long)mac[mac.length - 1] | 65280L & (long)mac[mac.length - 2] << 8) >> 6;
            }

            return id;
        } catch (SocketException var6) {
            throw new GetHardwareIdFailedException(var6);
        } catch (UnknownHostException var7) {
            throw new GetHardwareIdFailedException(var7);
        }
    }

    public static void main(String[] args) throws GetHardwareIdFailedException, InvalidSystemClockException {
        BasicEntityIdGenerator generator = new BasicEntityIdGenerator((long)((new Random()).nextInt(1022) + 1));
        BasicEntityIdGenerator generator2 = new BasicEntityIdGenerator((long)((new Random()).nextInt(1022) + 1));
        int n = 10000000;
        Set<Long> ids = new HashSet();

        for(int i = 0; i < n; ++i) {
            Long id = generator.generateLongId();
            Long id2 = generator2.generateLongId();
            if (ids.contains(id)) {
                System.out.println("Duplicate id:" + id);
            }
            ids.add(id);
            ids.add(id2);
        }
    }

}
