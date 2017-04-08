package org.pudding.transport.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static org.pudding.transport.netty.TcpChannelFactory.ChannelType.ACCEPTOR;
import static org.pudding.transport.netty.TcpChannelFactory.ChannelType.CONNECTOR;
import static org.pudding.transport.netty.TcpChannelFactory.IOType.EPOLL;
import static org.pudding.transport.netty.TcpChannelFactory.IOType.NIO;

/**
 * The factory of TCP channel.
 *
 * @author Yohann.
 */
public class TcpChannelFactory<T extends Channel> implements ChannelFactory<T> {

    // Acceptor
    public static final ChannelFactory<ServerChannel> NIO_FACTORY_ACCEPTRO = new TcpChannelFactory<>(NIO, ACCEPTOR);
    public static final ChannelFactory<ServerChannel> EPOLL_FACTORY_ACCEPTRO = new TcpChannelFactory<>(EPOLL, ACCEPTOR);

    // Connector
    public static final ChannelFactory<Channel> NIO_FACTORY_CONNECTOR = new TcpChannelFactory<>(NIO, CONNECTOR);
    public static final ChannelFactory<Channel> EPOLL_FACTORY_CONNECTOR = new TcpChannelFactory<>(EPOLL, CONNECTOR);

    private final IOType ioType;
    private final ChannelType channelType;

    public TcpChannelFactory(IOType ioType, ChannelType channelType) {
        this.ioType = ioType;
        this.channelType = channelType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newChannel() {
        switch (channelType) {
            case ACCEPTOR:
                switch (ioType) {
                    case NIO:
                        return (T) new NioServerSocketChannel();
                    case EPOLL:
                        return (T) new EpollServerSocketChannel();
                    default:
                        throw new IllegalStateException("invalid IO type: " + ioType);
                }

            case CONNECTOR:
                switch (ioType) {
                    case NIO:
                        return (T) new NioSocketChannel();
                    case EPOLL:
                        return (T) new EpollSocketChannel();
                    default:
                        throw new IllegalStateException("invalid IO type: " + ioType);
                }

            default:
                throw new IllegalStateException("invalid channel type: " + channelType);
        }
    }

    public enum IOType {
        NIO,
        EPOLL
    }

    public enum ChannelType {
        ACCEPTOR,
        CONNECTOR
    }
}
