package com.defaulttenant.hotswap.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.defaulttenant.hotswap.exception.HttpCodeException;

/**
 * 雪花算法，为避免前端js精度问题生成16位
 * 0000000000 0 - 0000000000 0000000000 0000000000 0000000000 - 00000 - 00000000
 * 11位标签-              41位時間戳 -                         5位数据中心 -  8位顺序位
 * 注：
 * 1. 生成ID縮短至16位
 * 2. 41位時間戳可使用69年(2^41/356/24/60/60/1000)
 * 3. 5位数据中心，可以支持部署32个节点
 * 4. 8位顺序，每毫秒生成256个ID
 * <p>
 * 1111111111111111111111111111111 111111111111 1111111111
 * 0001110001101011111101010010011 000110100000 0000000000
 * 0000000000000000000000000000001 000000000001 0000000001
 * @author sys
 * @date 2021-08-09 20:38
 */
public class SnowflakeIdWorker {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIdWorker.class);
    private static long defaultServiceSequence = -1L;
    /**
     * 服務序列號
     */
    private long serviceSequence;

    /**
     * 時間戳開始計時時間 2013-04-08 04:48:16
     */
    private final long startTimestamp = 1365367696000L;

    /**
     * 默认顺序位的初始值
     */
    private long sequence = 1L;
    /**
     * 順序位2进制长度
     */
    private final long sequenceBits = 8L;

    /**
     * 节点id2进制长度
     */
    private final long serviceSequenceBits = 5L;


    /**
     * 节点id右移位數
     */
    private final long serviceSequenceShift = sequenceBits;

    /**
     * 時間戳右移位數
     */
    private final long timestampShift = sequenceBits + serviceSequenceBits;

    /**
     * 生成序列的掩码(8位所对应的最大整数值)，这里为256 (0b1111111111=1023)
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private SnowflakeIdWorker() {
        if (-1L != defaultServiceSequence) {
            // 如果配置文件里设置了节点id则以配置文件为主
            serviceSequence = defaultServiceSequence;

            LOGGER.info("snowflake serviceSequence is {}", serviceSequence);
        } else {
            // 一种比较简单的节点id自动生成策略，根据节点ip数值化后取余得到，只能尽量降低冲突情况，无法规避
            String ip = NetWorkUtils.getHostIpV4Address();
            serviceSequence = NetWorkUtils.ipV4AddressToLong(ip) % (int) Math.pow(2, serviceSequenceBits);

            LOGGER.info("ip address is {}, snowflake serviceSequence is {}", ip, serviceSequence);
        }
    }

    private SnowflakeIdWorker(Long serviceSequence) {
        this.serviceSequence = serviceSequence;
    }

    /**
     * 生成唯一的趨勢遞增的id
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            LOGGER.warn("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1L) & sequenceMask;
            if (sequence == 0L) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - startTimestamp) << timestampShift) | (serviceSequence << serviceSequenceShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }


    private static volatile SnowflakeIdWorker instance;

    public static SnowflakeIdWorker getInstance() {
        if (null == instance) {
            synchronized (SnowflakeIdWorker.class) {
                if (null == instance) {
                    instance = new SnowflakeIdWorker();
                }
            }
        }

        return instance;
    }

    public static SnowflakeIdWorker getInstance(Long serviceSequence) {
        if (null == instance) {
            synchronized (SnowflakeIdWorker.class) {
                if (null == instance) {
                    instance = new SnowflakeIdWorker(serviceSequence);
                }
            }
        }

        return instance;
    }

    public static void setDefaultServiceSequence(Long serviceSequence) {
        if (null != serviceSequence) {
            defaultServiceSequence = serviceSequence;
        }
    }
}