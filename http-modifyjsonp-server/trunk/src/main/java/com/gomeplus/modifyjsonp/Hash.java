package com.gomeplus.modifyjsonp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by xue on 11/25/16.
 */

class Hash {
    /**
     * 返回optionalValues 中的任意一个数值
     * @param input
     * @param optionalValues
     * @return
     */
    public static <T> T getHashCode(String input, T[] optionalValues) {
        int index = (int) Math.abs((murmurHash(input) % optionalValues.length));
        return optionalValues[index];
    }
    /**
     * 执行murmur hash 算法，返回值可能为负数
     * @param input
     * @return long
     */
    private static long murmurHash(String input) {
        ByteBuffer buf = ByteBuffer.wrap(input.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }
}
