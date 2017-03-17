package org.pudding.transport;

/**
 * Socket选项.
 *
 * @author Yohann.
 */
public class SocketOption<T> extends Options {

    /** read()阻塞时长 */
    public static final SocketOption<Integer> SO_TIMEOUT        = valueOf("SO_TIMEOUT");

    /** 发送缓冲区大小 */
    public static final SocketOption<Integer> SO_SNDBUF         = valueOf("SO_SNDBUF");

    /** 接收缓冲区大小 */
    public static final SocketOption<Integer> SO_RCVBUF         = valueOf("SO_RCVBUF");

    /** Nagle算法，开启情况下，TCP为了提高数据传输效率，发送端会将许多小数据包合并发送（并不是立即发送）*/
    public static final SocketOption<Boolean> TCP_NODELAY       = valueOf("TCP_NODELAY");

    /** TCP保活，两小时之内套接字任意方向没有数据交换的情况下，TCP给对端发送探测数据包 */
    public static final SocketOption<Boolean> SO_KEEPALIVE      = valueOf("SO_KEEPALIVE");

    /** close()滞留时间，直到客户端数据和FIN已被服务端TCP确认才返回 */
    public static final SocketOption<Integer> SO_LINGER         = valueOf("SO_LINGER");

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    protected static <T> SocketOption<T> valueOf(String name) {
        SocketOption<?> option = socketOptionMap.get(name);
        if (option == null) {
            option = new SocketOption<>();
            socketOptionMap.put(name, option);
        }
        return (SocketOption<T>) option;
    }
}
