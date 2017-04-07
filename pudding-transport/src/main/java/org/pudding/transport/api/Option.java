package org.pudding.transport.api;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import org.pudding.common.utils.Constant;
import org.pudding.common.utils.ConstantPool;

/**
 * Pudding Option.
 *
 * @author Yohann.
 */
public class Option<T> implements Constant {

    private final String name; // Constant name

    private static final ConstantPool<Option<?>> pool = new ConstantPool<Option<?>>() {
        @Override
        protected Option<?> newInstant(String name) {
            return new Option<>(name);
        }
    };

    public Option(String name) {
        this.name = name;
    }

    // TCP Options

    /**
     * 未完成连接队列和已完成连接队列的总长度
     */
    public static final Option<Integer> SO_BACKLOG = valueOf("SO_BACKLOG");

    /**
     * Nagle算法，开启情况下，TCP为了提高数据传输效率，发送端会将许多小数据包合并发送（并不是立即发送）
     */
    public static final Option<Boolean> TCP_NODELAY = valueOf("TCP_NODELAY");

    /**
     * 发送缓冲区大小
     */
    public static final Option<Integer> SO_SNDBUF = valueOf("SO_SNDBUF");

    /**
     * 接收缓冲区大小
     */
    public static final Option<Integer> SO_RCVBUF = valueOf("SO_RCVBUF");

    public static final Option<Boolean> SO_REUSEADDR = valueOf("SO_REUSEADDR");

    /**
     * TCP保活，两小时之内套接字任意方向没有数据交换的情况下，TCP给对端发送探测数据包
     */
    public static final Option<Boolean> SO_KEEPALIVE = valueOf("SO_KEEPALIVE");

    /**
     * close()滞留时间，直到客户端数据和FIN已被服务端TCP确认才返回
     */
    public static final Option<Integer> SO_LINGER = valueOf("SO_LINGER");

    public static final Option<Integer> IP_TOS = valueOf("IP_TOS");

    public static final Option<Boolean> ALLOW_HALF_CLOSURE = valueOf("ALLOW_HALF_CLOSURE");

    // Netty Options

    public static final Option<Integer> CONNECT_TIMEOUT_MILLIS = valueOf("CONNECT_TIMEOUT_MILLIS");

    public static final Option<Integer> MAX_MESSAGES_PER_READ = valueOf("MAX_MESSAGES_PER_READ");

    public static final Option<Integer> WRITE_SPIN_COUNT = valueOf("WRITE_SPIN_COUNT");

    public static final Option<ByteBufAllocator> ALLOCATOR = valueOf("ALLOCATOR");

    public static final Option<RecvByteBufAllocator> RCVBUF_ALLOCATOR = valueOf("RCVBUF_ALLOCATOR");

    public static final Option<Boolean> AUTO_READ = valueOf("AUTO_READ");

    public static final Option<Boolean> AUTO_CLOSE = valueOf("AUTO_CLOSE");

    public static final Option<Integer> WRITE_BUFFER_HIGH_WATER_MARK = valueOf("WRITE_BUFFER_HIGH_WATER_MARK");

    public static final Option<Integer> WRITE_BUFFER_LOW_WATER_MARK = valueOf("WRITE_BUFFER_LOW_WATER_MARK");

    public static final Option<WriteBufferWaterMark> WRITE_BUFFER_WATER_MARK = valueOf("WRITE_BUFFER_WATER_MARK");

    public static final Option<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = valueOf("MESSAGE_SIZE_ESTIMATOR");

    public static final Option<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = valueOf("SINGLE_EVENTEXECUTOR_PER_GROUP");

    /**
     * Return the {@link Option} that given name.
     */
    @SuppressWarnings("unchecked")
    public static <T> Option<T> valueOf(String name) {
        return (Option<T>) pool.valueOf(name);
    }

    @Override
    public String name() {
        return name;
    }
}
