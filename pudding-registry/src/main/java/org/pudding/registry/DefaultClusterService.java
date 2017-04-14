package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.protocol.Message;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTcpConnector;
import org.pudding.transport.netty.NettyTransportFactory;

import java.net.SocketAddress;

/**
 * The default implementation of {@link ClusterService}.
 *
 * @author Yohann.
 */
public class DefaultClusterService implements ClusterService {
    private static final Logger logger = Logger.getLogger(DefaultClusterService.class);

    // Process the cluster task
    private final Processor clusterProcessor = new RegistryProcessor();

    private final Connector connector = NettyTransportFactory.createTcpConnector();

    // Cluster channel
    private volatile Channel channel;

    private volatile boolean isShutdown = false;

    public DefaultClusterService() {
        connector.withProcessor(clusterProcessor);
    }

    @Override
    public void connectCluster(SocketAddress... prevAddress) {
        checkNotShutdown();

        try {
            // Connect to last server of cluster
            channel = connector.connect(NettyTcpConnector.ReconnPattern.CONNECT_PREVIOUS_ADDRESS, prevAddress);

            logger.info("connect with registry cluster server, channel: " + channel);
        } catch (InterruptedException e) {
            logger.warn("connect with registry cluster server failed, channel: " + channel);
        }
    }

    @Override
    public void disconnectCluster() {
        if (channel != null) {
            channel.close();
            logger.info("disconnect with registry cluster server, channel:" + channel);
        }
    }

    @Override
    public void shutdown() {
        disconnectCluster();
        connector.shutdownGracefully();
        isShutdown = true;
    }

    private void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    /**
     * The processor about cluster.
     */
    private class RegistryProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }

        @Override
        public void handleConnection(Channel channel) {
            DefaultClusterService.this.channel = channel;
        }

        @Override
        public void handleDisconnection(Channel channel) {

        }
    }
}
