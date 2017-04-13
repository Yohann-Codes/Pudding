package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.protocol.Message;
import org.pudding.common.utils.Maps;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTransportFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of {@link ClusterService}.
 *
 * @author Yohann.
 */
public class DefaultClusterService implements ClusterService {
    private static final Logger logger = Logger.getLogger(DefaultClusterService.class);

    // Process the cluster task
    private static final Processor CLUSTER_PROCESSOR = new RegistryProcessor();

    private final Connector connector = NettyTransportFactory.createTcpConnector();

    // Other registry server channel
    private final ConcurrentMap<SocketAddress, Channel> registryServers = Maps.newConcurrentHashMap();

    private volatile boolean isShutdown = false;

    public DefaultClusterService() {
        connector.withProcessor(CLUSTER_PROCESSOR);
    }

    @Override
    public void connectCluster(SocketAddress... clusterAddress) {
        checkNotShutdown();

        for (SocketAddress address : clusterAddress) {
            synchronized (this) {
                try {
                    // Connect to one server of cluster
                    Channel channel = connector.connect(address);
                    registryServers.put(address, channel);

                    logger.info("connect with registry cluster server: " + address);
                } catch (InterruptedException e) {
                    logger.warn("connect with registry cluster server failed: " + address);
                }
            }
        }
    }

    @Override
    public void disconnectCluster() {
        for (Map.Entry<SocketAddress, Channel> entry : registryServers.entrySet()) {
            synchronized (registryServers) {
                entry.getValue().close();
                registryServers.remove(entry.getKey());
                logger.info("disconnect with registry cluster server: " + entry.getKey());
            }
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
    private static class RegistryProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }
    }
}
