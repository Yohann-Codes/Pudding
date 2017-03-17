package org.pudding.transport;

/**
 * ServerSocket选项.
 *
 * @author Yohann.
 */
public class SevSocketOption<T> extends Options {

    /** accpet()阻塞时长 */
    public static final SevSocketOption<Integer> SO_TIMEOUT     = valueOf("SO_TIMEOUT");

    /** 未完成连接队列和已完成连接队列的总长度 */
    public static final SevSocketOption<Integer> SO_BACKLOG     = valueOf("SO_BACKLOG");

    /** 在此ServerSocket上接收的Socket接收缓冲区的大小 */
    public static final SevSocketOption<Integer> SO_RCVBUF      = valueOf("SO_RCVBUF");

    /** 启动该选项后，TIME_WAIT状态未结束期间可以绑定该端口 */
    public static final SevSocketOption<Boolean> SO_REUSEADDR   = valueOf("SO_REUSEADDR");

    private T value;

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    protected static <T> SevSocketOption<T> valueOf(String name) {
        SevSocketOption<?> option = sevSocketOptionMap.get(name);
        if (option == null) {
            option = new SevSocketOption<>();
            sevSocketOptionMap.put(name, option);
        }
        return (SevSocketOption<T>) option;
    }
}
