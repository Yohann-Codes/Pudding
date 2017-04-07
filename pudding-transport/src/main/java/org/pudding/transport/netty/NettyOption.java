package org.pudding.transport.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import org.pudding.common.utils.Lists;
import org.pudding.transport.api.OptionConfig;
import org.pudding.transport.api.Option;

import java.util.Collections;
import java.util.List;

/**
 * Netty option configuration.
 *
 * @author Yohann.
 */
public class NettyOption implements OptionConfig {
    private volatile int connectTimeoutMillis = -1;
    private volatile int maxMessagesPerRead = -1;
    private volatile int writeSpinCount = -1;
    private volatile ByteBufAllocator allocator = null;
    private volatile RecvByteBufAllocator recvByteBufAllocator = null;
    private volatile boolean autoRead = true;
    private volatile boolean autoClose = true;
    private volatile int writeBufferHighWaterMark = -1;
    private volatile int writeBufferLowWaterMark = -1;
    private volatile WriteBufferWaterMark writeBufferWaterMark = null;
    private volatile MessageSizeEstimator messageSizeEstimator = null;
    private volatile boolean singleEventExecutorPerGroup = true;

    @Override
    public <T> boolean setOption(Option<T> option, T value) {
        validate(option, value);

        if (option == Option.CONNECT_TIMEOUT_MILLIS) {
            setConnectTimeoutMillis((Integer) value);
        } else if (option == Option.MAX_MESSAGES_PER_READ) {
            setMaxMessagesPerRead((Integer) value);
        } else if (option == Option.WRITE_SPIN_COUNT) {
            setWriteSpinCount((Integer) value);
        } else if (option == Option.ALLOCATOR) {
            setAllocator((ByteBufAllocator) value);
        } else if (option == Option.RCVBUF_ALLOCATOR) {
            setRecvByteBufAllocator((RecvByteBufAllocator) value);
        } else if (option == Option.AUTO_READ) {
            setAutoRead((Boolean) value);
        } else if (option == Option.AUTO_CLOSE) {
            setAutoClose((Boolean) value);
        } else if (option == Option.WRITE_BUFFER_HIGH_WATER_MARK) {
            setWriteBufferHighWaterMark((Integer) value);
        } else if (option == Option.WRITE_BUFFER_LOW_WATER_MARK) {
            setWriteBufferLowWaterMark((Integer) value);
        } else if (option == Option.WRITE_BUFFER_WATER_MARK) {
            setWriteBufferWaterMark((WriteBufferWaterMark) value);
        } else if (option == Option.MESSAGE_SIZE_ESTIMATOR) {
            setMessageSizeEstimator((MessageSizeEstimator) value);
        } else if (option == Option.SINGLE_EVENTEXECUTOR_PER_GROUP) {
            setSingleEventExecutorPerGroup((Boolean) value);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public List<Option<?>> getOptions() {
        return getOptions0(null,
                Option.CONNECT_TIMEOUT_MILLIS,
                Option.MAX_MESSAGES_PER_READ,
                Option.WRITE_SPIN_COUNT,
                Option.ALLOCATOR,
                Option.RCVBUF_ALLOCATOR,
                Option.AUTO_READ,
                Option.AUTO_CLOSE,
                Option.WRITE_BUFFER_HIGH_WATER_MARK,
                Option.WRITE_BUFFER_LOW_WATER_MARK,
                Option.WRITE_BUFFER_WATER_MARK,
                Option.MESSAGE_SIZE_ESTIMATOR,
                Option.SINGLE_EVENTEXECUTOR_PER_GROUP);
    }

    protected List<Option<?>> getOptions0(List<Option<?>> result, Option<?>... options) {
        if (result == null) {
            result = Lists.newArrayList();
        }
        Collections.addAll(result, options);
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOption(Option<T> option) {
        checkNotNull(option, "option");

        if (option == Option.CONNECT_TIMEOUT_MILLIS) {
            return (T) Integer.valueOf(getConnectTimeoutMillis());
        }
        if (option == Option.MAX_MESSAGES_PER_READ) {
            return (T) Integer.valueOf(getMaxMessagesPerRead());
        }
        if (option == Option.WRITE_SPIN_COUNT) {
            return (T) Integer.valueOf(getWriteSpinCount());
        }
        if (option == Option.ALLOCATOR) {
            return (T) getAllocator();
        }
        if (option == Option.RCVBUF_ALLOCATOR) {
            return (T) getRecvByteBufAllocator();
        }
        if (option == Option.AUTO_READ) {
            return (T) Boolean.valueOf(getAutoRead());
        }
        if (option == Option.AUTO_CLOSE) {
            return (T) Boolean.valueOf(getAutoClose());
        }
        if (option == Option.WRITE_BUFFER_HIGH_WATER_MARK) {
            return (T) Integer.valueOf(getWriteBufferHighWaterMark());
        }
        if (option == Option.WRITE_BUFFER_LOW_WATER_MARK) {
            return (T) Integer.valueOf(getWriteBufferLowWaterMark());
        }
        if (option == Option.WRITE_BUFFER_WATER_MARK) {
            return (T) getWriteBufferWaterMark();
        }
        if (option == Option.MESSAGE_SIZE_ESTIMATOR) {
            return (T) getMessageSizeEstimator();
        }
        if (option == Option.SINGLE_EVENTEXECUTOR_PER_GROUP) {
            return (T) Boolean.valueOf(isSingleEventExecutorPerGroup());
        }

        return null;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setMaxMessagesPerRead(int maxMessagesPerRead) {
        this.maxMessagesPerRead = maxMessagesPerRead;
    }

    public void setWriteSpinCount(int writeSpinCount) {
        this.writeSpinCount = writeSpinCount;
    }

    public void setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
    }

    public void setRecvByteBufAllocator(RecvByteBufAllocator recvByteBufAllocator) {
        this.recvByteBufAllocator = recvByteBufAllocator;
    }

    public void setAutoRead(boolean autoRead) {
        this.autoRead = autoRead;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public void setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }

    public void setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }

    public void setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        this.writeBufferWaterMark = writeBufferWaterMark;
    }

    public void setMessageSizeEstimator(MessageSizeEstimator messageSizeEstimator) {
        this.messageSizeEstimator = messageSizeEstimator;
    }

    public void setSingleEventExecutorPerGroup(boolean singleEventExecutorPerGroup) {
        this.singleEventExecutorPerGroup = singleEventExecutorPerGroup;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getMaxMessagesPerRead() {
        return maxMessagesPerRead;
    }

    public int getWriteSpinCount() {
        return writeSpinCount;
    }

    public ByteBufAllocator getAllocator() {
        return allocator;
    }

    public RecvByteBufAllocator getRecvByteBufAllocator() {
        return recvByteBufAllocator;
    }

    public boolean getAutoRead() {
        return autoRead;
    }

    public boolean getAutoClose() {
        return autoClose;
    }

    public int getWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark;
    }

    public int getWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark;
    }

    public WriteBufferWaterMark getWriteBufferWaterMark() {
        return writeBufferWaterMark;
    }

    public MessageSizeEstimator getMessageSizeEstimator() {
        return messageSizeEstimator;
    }

    public boolean isSingleEventExecutorPerGroup() {
        return singleEventExecutorPerGroup;
    }

    protected  <T> void validate(Option<T> option, T value) {
        checkNotNull(option, "option");
        checkNotNull(value, "value");
    }

    protected <T> void checkNotNull(T reference, String msg) {
        if (reference == null) {
            throw new NullPointerException(msg);
        }
    }
}
