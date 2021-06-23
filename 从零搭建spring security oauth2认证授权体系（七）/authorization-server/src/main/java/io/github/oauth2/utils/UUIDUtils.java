package io.github.oauth2.utils;

import java.util.Random;

/**
 * UUIDUtils.
 *
 * @author onlyonezhongjinhui
 */
public final class UUIDUtils {

    public static void main(String[] args) {
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
        System.out.println(UUIDUtils.getInstance().generateShortUuid());
    }

    private static final Random RANDOM = new Random();

    private static final long WORKER_ID_BITS = 5L;

    private static final long DATA_CENTER_ID_BITS = 5L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    private static final long SEQUENCE_BITS = 12L;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final UUIDUtils ID_WORKER_UTILS = new UUIDUtils();

    private final long workerId;

    private final long dataCenterId;

    private final long epoch;

    private long sequence = '0';

    private long lastTimestamp = -1L;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static UUIDUtils getInstance() {
        return ID_WORKER_UTILS;
    }

    /**
     * generate short uuid.
     *
     * @return short uuid.
     */
    public String generateShortUuid() {
        return String.valueOf(ID_WORKER_UTILS.nextId());
    }

    private UUIDUtils() {
        this(RANDOM.nextInt((int) MAX_WORKER_ID), RANDOM.nextInt((int) MAX_DATA_CENTER_ID), 1288834974657L);
    }

    private UUIDUtils(final long workerId, final long dataCenterId, final long epoch) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("data center Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.epoch = epoch;
    }

    private synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - epoch) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

}
