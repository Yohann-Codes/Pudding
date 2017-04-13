package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.Lists;
import org.pudding.common.utils.Maps;
import org.pudding.common.utils.ServiceLoaderUtil;
import org.pudding.rpc.RegistryService;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.netty.NettyTransportFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * The default implementation of {@link ServiceProvider}.
 * <p>
 * Suggest use singleton.
 *
 * @author Yohann.
 */
public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = Logger.getLogger(DefaultServiceProvider.class);

    // Bind service
    private final Acceptor acceptor = NettyTransportFactory.createTcpAcceptor();
    // Create instance Based on SPI
    private final RegistryService registryService = ServiceLoaderUtil.loadFirst(RegistryService.class);
    private final ClientService clientService = new DefaultClientService();

    // Hold service that has started
    private final ConcurrentMap<ServiceMeta, Channel> startedServices = Maps.newConcurrentHashMap();

    private volatile boolean allPublished = false;

    @Override
    public ServiceProvider connectRegistry() {
        String[] stringAddress = ProviderConfig.getRegistryAddress();
        checkAddress(stringAddress);
        SocketAddress[] address = parseToSocketAddress(stringAddress);
        registryService.connectRegistry(address);
        return this;
    }

    @Override
    public ServiceProvider connectRegistry(String... registryAddress) {
        checkAddress(registryAddress);
        SocketAddress[] address = parseToSocketAddress(registryAddress);
        registryService.connectRegistry(address);
        return this;
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
    public ServiceProvider startService(ServiceMeta serviceMeta) {
        doStart(serviceMeta);
        return this;
    }

    @Override
    public ServiceProvider startServices(ServiceMeta... serviceMeta) {
        for (ServiceMeta meta : serviceMeta) {
            doStart(meta);
        }
        return this;
    }

    private void doStart(ServiceMeta serviceMeta) {
        synchronized (this) {
            Channel channel = clientService.startService(serviceMeta);
            startedServices.put(serviceMeta, channel);
        }
    }

    @Override
    public ServiceProvider publicService(ServiceMeta serviceMeta) {
        if (!startedServices.containsKey(serviceMeta)) {
            // Service not start
            throw new IllegalStateException("service not start: " + serviceMeta);
        }
        registryService.register(serviceMeta);
        return this;
    }

    @Override
    public ServiceProvider publicServices(ServiceMeta... serviceMeta) {
        for (ServiceMeta meta : serviceMeta) {
            if (!startedServices.containsKey(meta)) {
                // Service not start
                throw new IllegalStateException("service not start: " + meta);
            }
        }

        // Entrue all services have started
        for (ServiceMeta meta : serviceMeta) {
            registryService.register(meta);
        }
        return this;
    }

    @Override
    public ServiceProvider publishAllService() {
        synchronized (this) {
            if (allPublished) {
                throw new IllegalStateException("publicAllService() can be called only once");
            }
            for (Map.Entry<ServiceMeta, Channel> entry : startedServices.entrySet()) {
                ServiceMeta meta = entry.getKey();
                registryService.register(meta);
            }
            allPublished = true;
        }
        return this;
    }

    @Override
    public ServiceProvider unpulishService(ServiceMeta serviceMeta) {
        return this;
    }

    @Override
    public ServiceProvider unpulishServices(ServiceMeta... serviceMeta) {
        return this;
    }

    @Override
    public ServiceProvider unpublishAllService() {
        return this;
    }

    @Override
    public ServiceProvider stopService(ServiceMeta serviceMeta) {
        doStop(serviceMeta);
        return this;
    }

    @Override
    public ServiceProvider stopServices(ServiceMeta... serviceMeta) {
        for (ServiceMeta meta : serviceMeta) {
            doStop(meta);
        }
        return this;
    }

    @Override
    public ServiceProvider stopAllService() {
        synchronized (startedServices) {
            for (Map.Entry<ServiceMeta, Channel> entry : startedServices.entrySet()) {
                ServiceMeta meta = entry.getKey();
                Channel channel = entry.getValue();
                clientService.stopService(meta, channel);
                startedServices.remove(meta);
            }
        }
        return this;
    }

    private void doStop(ServiceMeta serviceMeta) {
        if (!startedServices.containsKey(serviceMeta)) {
            throw new IllegalStateException("service not start: " + serviceMeta);
        }

        Channel channel = startedServices.get(serviceMeta);
        clientService.stopService(serviceMeta, channel);
        startedServices.remove(serviceMeta);
    }

    @Override
    public void shutdown() {
        registryService.shutdown();
        clientService.shutdown();
    }

    private void checkAddress(String[] address) {
        if (address == null || address.length < 1) {
            throw new IllegalStateException("invalid registry address, please check address");
        }
    }
}
