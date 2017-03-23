package org.pudding.transport.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;

/**
 * 配置Netty选项.
 *
 * @author Yohann.
 */
public class ConfigOptions {

    protected ServerBootstrap serverBootstrap;
    protected Bootstrap bootstrap;
    protected Parent parent;
    protected Child child;
    public boolean accept;

    public ConfigOptions(boolean accept) {
        this.accept = accept;
        if (accept) {
            initAcceptor();
        } else {
            initConnector();
        }
    }

    private void initAcceptor() {
        serverBootstrap = new ServerBootstrap();
        parent = new Parent();
        child = new Child();
    }

    private void initConnector() {
        bootstrap = new Bootstrap();
        child = new Child();
    }

    public Object getBootstrap() {
        return bootstrap;
    }

    /**
     * parent channel options.
     */
    protected class Parent extends Netty {

        public void setSoBacklog(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_BACKLOG, value);
            } else {
                bootstrap.option(ChannelOption.SO_BACKLOG, value);
            }
        }

        public void setSoReuseaddr(boolean value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_REUSEADDR, value);
            } else {
                bootstrap.option(ChannelOption.SO_REUSEADDR, value);
            }
        }

        public void setSoRcvbuf(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_RCVBUF, value);
            } else {
                bootstrap.option(ChannelOption.SO_RCVBUF, value);
            }
        }
    }

    /**
     * child channel options.
     */
    protected class Child extends Netty {

        public void setSoSndbuf(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_SNDBUF, value);
            } else {
                bootstrap.option(ChannelOption.SO_SNDBUF, value);
            }
        }

        public void setSoRcvbuf(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_RCVBUF, value);
            } else {
                bootstrap.option(ChannelOption.SO_RCVBUF, value);
            }
        }

        public void setTcpNodelay(boolean value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.TCP_NODELAY, value);
            } else {
                bootstrap.option(ChannelOption.TCP_NODELAY, value);
            }
        }

        public void setSoKeepalive(boolean value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_KEEPALIVE, value);
            } else {
                bootstrap.option(ChannelOption.SO_KEEPALIVE, value);
            }
        }

        public void setSoReuseaddr(boolean value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_REUSEADDR, value);
            } else {
                bootstrap.option(ChannelOption.SO_REUSEADDR, value);
            }
        }

        public void setSoLinger(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SO_LINGER, value);
            } else {
                bootstrap.option(ChannelOption.SO_LINGER, value);
            }
        }
    }

    protected class Netty {

        public void setConnectTimeoutMillis(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, value);
            } else {
                bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, value);
            }
        }

        public void setWriteSpinCount(int value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.WRITE_SPIN_COUNT, value);
            } else {
                bootstrap.option(ChannelOption.WRITE_SPIN_COUNT, value);
            }
        }

        public void setAllocator(ByteBufAllocator value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.ALLOCATOR, value);
            } else {
                bootstrap.option(ChannelOption.ALLOCATOR, value);
            }
        }

        public void setRcvbufAllocator(RecvByteBufAllocator value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, value);
            } else {
                bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, value);
            }
        }

        public void setAutoRead(boolean value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.AUTO_READ, value);
            } else {
                bootstrap.option(ChannelOption.AUTO_READ, value);
            }
        }

        public void setWriteBufferWaterMark(WriteBufferWaterMark value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, value);
            } else {
                bootstrap.option(ChannelOption.WRITE_BUFFER_WATER_MARK, value);
            }
        }

        public void setMessageSizeEstimator(MessageSizeEstimator value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, value);
            } else {
                bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, value);
            }
        }

        public void setSingleEventexecutorPerGroup(boolean value) {
            if (accept) {
                serverBootstrap.option(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, value);
            } else {
                bootstrap.option(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, value);
            }
        }
    }
}
