package org.pudding.transport.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

/**
 * Pudding选项.
 *
 * @author Yohann.
 */
public class Option<T> {

    // TCP Options

    /**
     * 未完成连接队列和已完成连接队列的总长度
     */
    public static final Option<Integer> SO_BACKLOG = option(Name.SO_BACKLOG);

    /**
     * Nagle算法，开启情况下，TCP为了提高数据传输效率，发送端会将许多小数据包合并发送（并不是立即发送）
     */
    public static final Option<Boolean> TCP_NODELAY = option(Name.TCP_NODELAY);

    /**
     * 发送缓冲区大小
     */
    public static final Option<Integer> SO_SNDBUF = option(Name.SO_SNDBUF);

    /**
     * 接收缓冲区大小
     */
    public static final Option<Integer> SO_RCVBUF = option(Name.SO_RCVBUF);

    public static final Option<Boolean> SO_REUSEADDR = option(Name.SO_REUSEADDR);

    /**
     * TCP保活，两小时之内套接字任意方向没有数据交换的情况下，TCP给对端发送探测数据包
     */
    public static final Option<Boolean> SO_KEEPALIVE = option(Name.SO_KEEPALIVE);

    /**
     * close()滞留时间，直到客户端数据和FIN已被服务端TCP确认才返回
     */
    public static final Option<Integer> SO_LINGER = option(Name.SO_LINGER);


    // Netty Options

    public static final Option<Integer> CONNECT_TIMEOUT_MILLIS = option(Name.CONNECT_TIMEOUT_MILLIS);

    public static final Option<Integer> WRITE_SPIN_COUNT = option(Name.WRITE_SPIN_COUNT);

    public static final Option<ByteBufAllocator> ALLOCATOR = option(Name.ALLOCATOR);

    public static final Option<RecvByteBufAllocator> RCVBUF_ALLOCATOR = option(Name.RCVBUF_ALLOCATOR);

    public static final Option<Boolean> AUTO_READ = option(Name.AUTO_READ);

    public static final Option<WriteBufferWaterMark> WRITE_BUFFER_WATER_MARK = option(Name.WRITE_BUFFER_WATER_MARK);

    public static final Option<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = option(Name.MESSAGE_SIZE_ESTIMATOR);

    public static final Option<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = option(Name.SINGLE_EVENTEXECUTOR_PER_GROUP);

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> Option<T> option(String name) {
        Option<?> option = new Option<>();
        return (Option<T>) option;
    }

    /**
     * 选项名称.
     */
    public static class Name {
        // TCP
        public static final String SO_BACKLOG                        = "SO_BACKLOG";
        public static final String TCP_NODELAY                       = "TCP_NODELAY";
        public static final String SO_RCVBUF                         = "SO_RCVBUF";
        public static final String SO_SNDBUF                         = "SO_SNDBUF";
        public static final String SO_REUSEADDR                      = "SO_REUSEADDR";
        public static final String SO_KEEPALIVE                      = "SO_KEEPALIVE";
        public static final String SO_LINGER                         = "SO_LINGER";

        // Netty
        public static final String CONNECT_TIMEOUT_MILLIS            = "CONNECT_TIMEOUT_MILLIS";
        public static final String MAX_MESSAGES_PER_READ             = "MAX_MESSAGES_PER_READ";
        public static final String WRITE_SPIN_COUNT                  = "WRITE_SPIN_COUNT";
        public static final String ALLOCATOR                         = "ALLOCATOR";
        public static final String RCVBUF_ALLOCATOR                  = "RCVBUF_ALLOCATOR";
        public static final String AUTO_READ                         = "AUTO_READ";
        public static final String WRITE_BUFFER_HIGH_WATER_MARK      = "WRITE_BUFFER_HIGH_WATER_MARK";
        public static final String WRITE_BUFFER_LOW_WATER_MARK       = "WRITE_BUFFER_LOW_WATER_MARK";
        public static final String WRITE_BUFFER_WATER_MARK           = "WRITE_BUFFER_WATER_MARK";
        public static final String MESSAGE_SIZE_ESTIMATOR            = "MESSAGE_SIZE_ESTIMATOR";
        public static final String SINGLE_EVENTEXECUTOR_PER_GROUP    = "SINGLE_EVENTEXECUTOR_PER_GROUP";
    }
}
