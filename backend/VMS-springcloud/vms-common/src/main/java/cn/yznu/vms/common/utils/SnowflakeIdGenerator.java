package cn.yznu.vms.common.utils;

/**
 * 雪花算法ID生成器
 * 基于Twitter Snowflake算法实现，生成分布式唯一ID
 * <p>
 * ID结构 (64位):
 * - 1位符号位 (固定为0)
 * - 41位时间戳 (毫秒级，可用约69年)
 * - 5位数据中心ID (0~31)
 * - 5位工作机器ID (0~31)
 * - 12位序列号 (同一毫秒内可生成4096个ID)
 */
public class SnowflakeIdGenerator {

    // 起始时间戳 (2024-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1704067200000L;

    // 各部分位数
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // 最大值
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 左移位数
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long datacenterId;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     *
     * @param datacenterId 数据中心ID (0~31)
     * @param workerId     工作机器ID (0~31)
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("datacenterId 不能大于 %d 或小于 0", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("workerId 不能大于 %d 或小于 0", MAX_WORKER_ID));
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 生成下一个唯一ID (线程安全)
     *
     * @return 雪花ID
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 时钟回拨检测
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回拨，拒绝生成ID。回拨时间: %d 毫秒", lastTimestamp - currentTimestamp));
        }

        // 同一毫秒内，序列号递增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新的毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 组装ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 生成字符串格式的ID
     */
    public String nextIdStr() {
        return String.valueOf(nextId());
    }

    /**
     * 等待下一毫秒
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    // ===================== 静态单例模式 =====================

    private static volatile SnowflakeIdGenerator instance;

    /**
     * 获取默认实例 (datacenterId=1, workerId=1)
     */
    public static SnowflakeIdGenerator getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(1, 1);
                }
            }
        }
        return instance;
    }

    /**
     * 快捷方法：生成ID
     */
    public static long generateId() {
        return getInstance().nextId();
    }

    /**
     * 快捷方法：生成字符串ID
     */
    public static String generateIdStr() {
        return getInstance().nextIdStr();
    }
}
