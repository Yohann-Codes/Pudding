package org.pudding.transport.netty;

/**
 * Netty provides the native socket transport for Linux using JNI based on Epoll Edge Triggered(ET).
 * This transport has higher performance and produces less garbage.
 *
 * @author Yohann.
 */
public final class EpollSupport {

    private static final boolean SUPPORT_EPOLL;

    static {
        // epoll
        boolean epoll;
        try {
            Class.forName("io.netty.channel.epoll.Native");
            epoll = true;
        } catch (Throwable e) {
            epoll = false;
        }
        SUPPORT_EPOLL = epoll;
    }

    /**
     * The native socket transport for Linux using JNI.
     */
    public static boolean isSupportEpoll() {
        return SUPPORT_EPOLL;
    }
}
