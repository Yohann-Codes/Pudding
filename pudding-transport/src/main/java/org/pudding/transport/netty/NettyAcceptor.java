package org.pudding.transport.netty;

import org.pudding.transport.Options;
import org.pudding.transport.SevSocketOption;
import org.pudding.transport.SocketOption;
import org.pudding.transport.abstraction.Acceptor;
import org.pudding.transport.abstraction.PudChannelFuture;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 基于Netty的Acceptor实现.
 *
 * @author Yohann.
 */
public class NettyAcceptor extends Options implements Acceptor {

    @Override
    public PudChannelFuture bind(int port) {
        return bind(new InetSocketAddress(port));
    }

    @Override
    public PudChannelFuture bind(String host, int port) {
        return bind(new InetSocketAddress(host, port));
    }

    @Override
    public PudChannelFuture bind(SocketAddress local) {
        return null;
    }

    @Override
    public <T> Acceptor sevSocketOptions(SevSocketOption<T> option, T value) {
        validate(option, value);
        option.setValue(value);

        if (option == SevSocketOption.SO_TIMEOUT) {
            sevSocketOptionMap.put(SO_TIMEOUT, option);
        } else if (option == SevSocketOption.SO_BACKLOG) {
            sevSocketOptionMap.put(SO_BACKLOG, option);
        } else if (option == SevSocketOption.SO_RCVBUF) {
            sevSocketOptionMap.put(SO_RCVBUF, option);
        } else if (option == SevSocketOption.SO_REUSEADDR) {
            sevSocketOptionMap.put(SO_REUSEADDR, option);
        } else {
            throw new IllegalArgumentException("not support option");
        }
        return this;
    }

    @Override
    public <T> T sevSocketOption(SevSocketOption<T> option) {
        validate(option);
        SevSocketOption<?> sevSocketOption = null;

        if (option == SevSocketOption.SO_TIMEOUT) {
            sevSocketOption = sevSocketOptionMap.get(SO_TIMEOUT);
        } else if (option == SevSocketOption.SO_BACKLOG) {
            sevSocketOption = sevSocketOptionMap.get(SO_BACKLOG);
        } else if (option == SevSocketOption.SO_RCVBUF) {
            sevSocketOption = sevSocketOptionMap.get(SO_RCVBUF);
        } else if (option == SevSocketOption.SO_REUSEADDR) {
            sevSocketOption = sevSocketOptionMap.get(SO_REUSEADDR);
        } else {
            throw new IllegalArgumentException("not config option");
        }

        return (T) sevSocketOption.getValue();
    }

    @Override
    public <T> Acceptor socketOptions(SocketOption<T> option, T value) {
        validate(option, value);
        option.setValue(value);

        if (option == SocketOption.SO_TIMEOUT) {
            socketOptionMap.put(SO_TIMEOUT, option);
        } else if (option == SocketOption.SO_SNDBUF) {
            socketOptionMap.put(SO_SNDBUF, option);
        } else if (option == SocketOption.SO_RCVBUF) {
            socketOptionMap.put(SO_RCVBUF, option);
        } else if (option == SocketOption.TCP_NODELAY) {
            socketOptionMap.put(TCP_NODELAY, option);
        } else if (option == SocketOption.SO_KEEPALIVE) {
            socketOptionMap.put(SO_KEEPALIVE, option);
        } else if (option == SocketOption.SO_LINGER) {
            socketOptionMap.put(SO_LINGER, option);
        } else {
            throw new IllegalArgumentException("not support option");
        }
        return this;
    }

    @Override
    public <T> T socketOption(SocketOption<T> option) {
        validate(option);
        SocketOption<?> socketOption = null;

        if (option == SocketOption.SO_TIMEOUT) {
            socketOption = socketOptionMap.get(SO_TIMEOUT);
        } else if (option == SocketOption.SO_SNDBUF) {
            socketOption = socketOptionMap.get(SO_SNDBUF);
        } else if (option == SocketOption.SO_RCVBUF) {
            socketOption = socketOptionMap.get(SO_RCVBUF);
        } else if (option == SocketOption.TCP_NODELAY) {
            socketOption = socketOptionMap.get(TCP_NODELAY);
        } else if (option == SocketOption.SO_KEEPALIVE) {
            socketOption = socketOptionMap.get(SO_KEEPALIVE);
        } else if (option == SocketOption.SO_LINGER) {
            socketOption = socketOptionMap.get(SO_LINGER);
        } else {
            throw new IllegalArgumentException("not config option");
        }
        return (T) socketOption.getValue();
    }

    @Override
    public <T> Acceptor otherOptions(NettyOption<T> option, T value) {
        return null;
    }

    @Override
    public <T> T otherOption(NettyOption<T> option) {
        return null;
    }

    private <T> void validate(SevSocketOption<T> option, T value) {
        if (option == null || value == null) {
            throw new NullPointerException("option || value");
        }
    }

    private <T> void validate(SevSocketOption<T> option) {
        if (option == null) {
            throw new NullPointerException("option");
        }
    }

    private <T> void validate(SocketOption<T> option, T value) {
        if (option == null || value == null) {
            throw new NullPointerException("option || value");
        }
    }

    private <T> void validate(SocketOption<T> option) {
        if (option == null) {
            throw new NullPointerException("option");
        }
    }
}