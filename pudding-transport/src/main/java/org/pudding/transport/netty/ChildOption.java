package org.pudding.transport.netty;

import org.pudding.transport.api.Option;

import java.util.List;

/**
 * Child option configuration.
 *
 * @author Yohann.
 */
public class ChildOption extends NettyOption {
    private volatile int receiveBufferSize = -1;
    private volatile int sendBufferSize = -1;
    private volatile boolean tcpNoDelay = false;
    private volatile boolean keepAlive = false;
    private volatile boolean reuseAddress = false;
    private volatile int soLinger = -1;
    private volatile int trafficClass = -1;
    private volatile boolean allowHalfClosure = false;

    @Override
    public <T> boolean setOption(Option<T> option, T value) {
        validate(option, value);

        if (option == Option.SO_RCVBUF) {
            setReceiveBufferSize((Integer) value);
        } else if (option == Option.SO_SNDBUF) {
            setSendBufferSize((Integer) value);
        } else if (option == Option.TCP_NODELAY) {
            setTcpNoDelay((Boolean) value);
        } else if (option == Option.SO_KEEPALIVE) {
            setKeepAlive((Boolean) value);
        } else if (option == Option.SO_REUSEADDR) {
            setReuseAddress((Boolean) value);
        } else if (option == Option.SO_LINGER) {
            setSoLinger((Integer) value);
        } else if (option == Option.IP_TOS) {
            setTrafficClass((Integer) value);
        } else if (option == Option.ALLOW_HALF_CLOSURE) {
            setAllowHalfClosure((Boolean) value);
        } else {
            return super.setOption(option, value);
        }

        return true;
    }

    @Override
    public List<Option<?>> getOptions() {
        return getOptions0(super.getOptions(),
                Option.SO_RCVBUF,
                Option.SO_SNDBUF,
                Option.TCP_NODELAY,
                Option.SO_KEEPALIVE,
                Option.SO_REUSEADDR,
                Option.SO_LINGER,
                Option.IP_TOS,
                Option.ALLOW_HALF_CLOSURE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOption(Option<T> option) {
        checkNotNull(option, "option");

        if (option == Option.SO_RCVBUF) {
            return (T) Integer.valueOf(getReceiveBufferSize());
        }
        if (option == Option.SO_SNDBUF) {
            return (T) Integer.valueOf(getSendBufferSize());
        }
        if (option == Option.TCP_NODELAY) {
            return (T) Boolean.valueOf(isTcpNoDelay());
        }
        if (option == Option.SO_KEEPALIVE) {
            return (T) Boolean.valueOf(isKeepAlive());
        }
        if (option == Option.SO_REUSEADDR) {
            return (T) Boolean.valueOf(isReuseAddress());
        }
        if (option == Option.IP_TOS) {
            return (T) Integer.valueOf(getTrafficClass());
        }
        if (option == Option.ALLOW_HALF_CLOSURE) {
            return (T) Boolean.valueOf(isAllowHalfClosure());
        }

        return super.getOption(option);
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setReuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public void setTrafficClass(int trafficClass) {
        this.trafficClass = trafficClass;
    }

    public void setAllowHalfClosure(boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public boolean isReuseAddress() {
        return reuseAddress;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public int getTrafficClass() {
        return trafficClass;
    }

    public boolean isAllowHalfClosure() {
        return allowHalfClosure;
    }
}
