package org.pudding.transport.netty;

import org.pudding.transport.api.Option;

import java.util.List;

/**
 * Parent option configuration.
 *
 * @author Yohann.
 */
public class ParentOption extends NettyOption {
    private volatile int backlog = -1;
    private volatile int receiveBufferSize = -1;
    private volatile boolean reuseAddress = false;

    @Override
    public <T> boolean setOption(Option<T> option, T value) {
        validate(option, value);

        if (option == Option.SO_BACKLOG) {
            setBacklog((Integer) value);
        } else if (option == Option.SO_RCVBUF) {
            setReceiveBufferSize((Integer) value);
        } else if (option == Option.SO_REUSEADDR) {
            setReuseAddress((Boolean) value);
        } else {
            return super.setOption(option, value);
        }

        return true;
    }

    @Override
    public List<Option<?>> getOptions() {
        return getOptions0(super.getOptions(),
                Option.SO_BACKLOG,
                Option.SO_RCVBUF,
                Option.SO_REUSEADDR);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOption(Option<T> option) {
        checkNotNull(option, "option");

        if (option == Option.SO_BACKLOG) {
            return (T) Integer.valueOf(getBacklog());
        } else if (option == Option.SO_RCVBUF) {
            return (T) Integer.valueOf(getReceiveBufferSize());
        } else if (option == Option.SO_REUSEADDR) {
            return (T) Boolean.valueOf(isReuseAddress());
        }

        return super.getOption(option);
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public int getBacklog() {
        return backlog;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }
}
