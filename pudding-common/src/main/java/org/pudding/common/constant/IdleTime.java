package org.pudding.common.constant;

/**
 * The network link to read and write free time.
 *
 * @author Yohann.
 */
public class IdleTime {

    /** Acceptor readerIdleTime, default: 63s, allow 3 seconds delay */
    public static final int READER_IDLE_TIME = 63;

    /** Connector writerIdleTime, default: 60s */
    public static final int WRITER_IDLE_TIME = 60;
}
