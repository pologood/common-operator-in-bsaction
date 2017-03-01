package com.gomeplus.oversea.bs.service.user.util;

/**
 * Created by liuliyun-ds on 2017/2/14.
 */
public class IdWorker {
    private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = 31L;
    private final long maxDatacenterId = 31L;
    private final long sequenceBits = 12L;
    private final long workerIdShift = 12L;
    private final long datacenterIdShift = 17L;
    private final long timestampLeftShift = 22L;
    private final long sequenceMask = 4095L;
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static IdWorker idWorker;

    private IdWorker(long workerId, long datacenterId) {
        if(workerId <= 31L && workerId >= 0L) {
            if(datacenterId <= 31L && datacenterId >= 0L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can\'t be greater than %d or less than 0", new Object[]{Long.valueOf(31L)}));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can\'t be greater than %d or less than 0", new Object[]{Long.valueOf(31L)}));
        }
    }

    public static synchronized Long getNextId() {
        if(idWorker == null) {
            idWorker = new IdWorker(0L, 0L);
        }

        return Long.valueOf(idWorker.nextId());
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if(timestamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", new Object[]{Long.valueOf(this.lastTimestamp - timestamp)}));
        } else {
            if(this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & 4095L;
                if(this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - 1288834974657L << 22 | this.datacenterId << 17 | this.workerId << 12 | this.sequence;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
            ;
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
