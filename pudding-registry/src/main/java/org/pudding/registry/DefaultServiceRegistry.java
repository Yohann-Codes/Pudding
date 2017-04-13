package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.Lists;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.transport.api.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/**
 * The default implementation of {@link PuddingServiceRegistry}.
 *
 * @author Yohann.
 */
public class DefaultServiceRegistry implements PuddingServiceRegistry {
    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final ClusterService clusterService = new DefaultClusterService();
    private final ClientService clientService = new DefaultClientService();

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
        String[] stringAddress = RegistryConfig.getClusterAddress();
        checkAddress(stringAddress);
        SocketAddress[] address = parseToSocketAddress(stringAddress);
        clusterService.connectCluster(address);
    }

    @Override
    public void joinUpCluster(String... clusterAddress) {
        checkAddress(clusterAddress);
        SocketAddress[] address = parseToSocketAddress(clusterAddress);
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
    public void shutdown() {
        clusterService.shutdown();
        clientService.shutdown();
    }

    @Override
    public void closeRegistry() {
        clientService.closeRegistry();
    }

    private void checkAddress(String[] address) {
        if (address == null || address.length < 1) {
            throw new IllegalStateException("invalid registry address, please check address");
        }
    }
}
