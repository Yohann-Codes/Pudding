package org.pudding.transport.netty;

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

    protected ServerBootstrap bootstrap;
    protected Parent parent;
    protected Child child;

    public ConfigOptions() {
        this.bootstrap = new ServerBootstrap();
        parent = new Parent();
        child = new Child();
    }

    /**
     * parent channel options.
     */
    protected class Parent extends Netty {
        public void setSoBacklog(int value) {
            bootstrap.option(ChannelOption.SO_BACKLOG, value);
        }

        public void setSoReuseaddr(boolean value) {
            bootstrap.option(ChannelOption.SO_REUSEADDR, value);
        }

        public void setSoRcvbuf(int value) {
            bootstrap.option(ChannelOption.SO_RCVBUF, value);
        }
    }

    /**
     * child channel options.
     */
    protected class Child extends Netty {

        public void setSoSndbuf(int value) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, value);
        }

        public void setSoRcvbuf(int value) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, value);
        }

        public void setTcpNodelay(boolean value) {
            bootstrap.childOption(ChannelOption.TCP_NODELAY, value);
        }

        public void setSoKeepalive(boolean value) {
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, value);
        }

        public void setSoReuseaddr(boolean value) {
            bootstrap.childOption(ChannelOption.SO_REUSEADDR, value);
        }

        public void setSoLinger(int value) {
            bootstrap.childOption(ChannelOption.SO_LINGER, value);
        }
    }

    protected class Netty {

        public void setConnectTimeoutMillis(int value) {
            bootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, value);
        }

        public void setWriteSpinCount(int value) {
            bootstrap.childOption(ChannelOption.WRITE_SPIN_COUNT, value);
        }

        public void setAllocator(ByteBufAllocator value) {
            bootstrap.childOption(ChannelOption.ALLOCATOR, value);
        }

        public void setRcvbufAllocator(RecvByteBufAllocator value) {
            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, value);
        }

        public void setAutoRead(boolean value) {
            bootstrap.childOption(ChannelOption.AUTO_READ, value);
        }

        public void setWriteBufferWaterMark(WriteBufferWaterMark value) {
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, value);
        }

        public void setMessageSizeEstimator(MessageSizeEstimator value) {
            bootstrap.childOption(ChannelOption.MESSAGE_SIZE_ESTIMATOR, value);
        }

        public void setSingleEventexecutorPerGroup(boolean value) {
            bootstrap.childOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, value);
        }
    }
}
