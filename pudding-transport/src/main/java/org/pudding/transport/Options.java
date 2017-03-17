package org.pudding.transport;

import org.pudding.transport.netty.NettyOption;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存TCP选项.
 *
 * @author Yohann.
 */
public class Options {

    // TCP
    public static final String SO_TIMEOUT      = "SO_TIMEOUT";
    public static final String SO_BACKLOG      = "SO_BACKLOG";
    public static final String SO_RCVBUF       = "SO_RCVBUF";
    public static final String SO_SNDBUF       = "SO_SNDBUF";
    public static final String SO_REUSEADDR    = "SO_REUSEADDR";
    public static final String TCP_NODELAY     = "TCP_NODELAY";
    public static final String SO_KEEPALIVE    = "SO_KEEPALIVE";
    public static final String SO_LINGER       = "SO_LINGER";

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

    /** ServerSocket */
    protected static Map<String, SevSocketOption<?>> sevSocketOptionMap = new HashMap<>();
    /** Socket */
    protected static Map<String, SocketOption<?>> socketOptionMap = new HashMap<>();

    /** netty */
    protected static Map<String, NettyOption<?>> nettyOptionMap = new HashMap<>();
}
