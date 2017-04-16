package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.Lists;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.transport.api.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The default implementation of {@link PuddingServiceRegistry}.
 *
 * @author Yohann.
 */
public class DefaultServiceRegistry implements PuddingServiceRegistry {
    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class);

    private ClusterService clusterService;
    private ClientService clientService;

    private int workers;
    private final ExecutorService executor;

    public DefaultServiceRegistry() {
        this(RegistryConfig.getWorkers());
    }

    public DefaultServiceRegistry(int workers) {
        validate(workers);
        this.workers = workers;
        executor = Executors.newFixedThreadPool(workers);
        initService();
    }

    private void initService() {
        clusterService = new DefaultClusterService(executor);
        clientService = new DefaultClientService(executor);
        clusterService.withClientService(clientService);
        clientService.withClusterService(clusterService);
    }

    private void validate(int workers) {
        if (workers < 1) {
            throw new IllegalArgumentException("workers: " + workers);
        }
    }

    @Override
    public Channel startRegistry(int port) {
        return startRegistry(new InetSocketAddress(port));
    }

    @Override
    public Channel startRegistry(SocketAddress localAddress) {
        return clientService.startRegistry(localAddress);
    }

    @Override
    public void joinUpCluster() {
        String[] prevAddress = RegistryConfig.getClusterAddress();
        SocketAddress[] address = parseToSocketAddress(prevAddress);
        clusterService.connectCluster(address);
    }

    @Override
    public void joinUpCluster(String... prevAddress) {
        SocketAddress[] address = parseToSocketAddress(prevAddress);
        clusterService.connectCluster(address);
    }

    private SocketAddress[] parseToSocketAddress(String... stringAddress) {
        List<SocketAddress> address = Lists.newArrayList();
        for (String addr : stringAddress) {
            AddressUtil.checkFormat(addr);
            String host = AddressUtil.host(addr);
            int port = AddressUtil.port(addr);
            address.add(new InetSocketAddress(host, port));
        }
        SocketAddress[] socketAddress = new SocketAddress[address.size()];
        for (int i = 0; i < socketAddress.length; i++) {
            socketAddress[i] = address.get(i);
        }
        return socketAddress;
    }

    @Override
    public void dropOutCluster() {
        clusterService.disconnectCluster();
    }

    @Override
    public int workers() {
        return workers;
    }

    @Override
    public void shutdown() {
        clusterService.shutdown();
        clientService.shutdown();
        executor.shutdown();
    }

    @Override
    public void closeRegistry() {
        clientService.closeRegistry();
    }
}
