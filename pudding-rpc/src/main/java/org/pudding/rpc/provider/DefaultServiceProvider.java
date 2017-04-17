package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.*;
import org.pudding.rpc.DefaultRegistryService;
import org.pudding.rpc.RegistryService;
import org.pudding.rpc.RpcConfig;
import org.pudding.transport.api.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The default implementation of {@link ServiceProvider}.
 * <p>
 * Suggest use singleton.
 *
 * @author Yohann.
 */
public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = Logger.getLogger(DefaultServiceProvider.class);

    // Hold service that has started
    private final ConcurrentMap<ServiceMeta, Channel> startedServices = Maps.newConcurrentHashMap();
    // Hold service that has published
    private final ConcurrentSet<ServiceMeta> publishedServices = Sets.newConcurrentSet();

    private volatile boolean allPublished = false;

    private RegistryService registryService;
    private ClientService clientService;

    private int workers;
    private final ExecutorService executor;

    public final Lock lock = new ReentrantLock();
    public final Condition notComplete = lock.newCondition();
    public volatile boolean timeout = true;

    public DefaultServiceProvider() {
        this(RpcConfig.getWorkers());
    }

    public DefaultServiceProvider(int workers) {
        validate(workers);
        this.workers = workers;
        executor = Executors.newFixedThreadPool(workers);
        initService();
    }

    private void initService() {
        registryService = new DefaultRegistryService(this, executor);
        clientService = new DefaultClientService(executor);
    }

    private void validate(int workers) {
        if (workers < 1) {
            throw new IllegalArgumentException("workers: " + workers);
        }
    }

    @Override
    public ServiceProvider connectRegistry() {
        String[] stringAddress = RpcConfig.getRegistryAddress();
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
        publishedServices.add(serviceMeta);

        // blocking
        waitCompletePublish();
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
            publishedServices.add(meta);
        }

        // blocking
        waitCompletePublish();
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
                publishedServices.add(meta);
            }
            allPublished = true;
        }

        // blocking
        waitCompletePublish();
        return this;
    }

    @Override
    public ServiceProvider unpulishService(ServiceMeta serviceMeta) {
        registryService.unregister(serviceMeta);
        publishedServices.remove(serviceMeta);
        return this;
    }

    @Override
    public ServiceProvider unpulishServices(ServiceMeta... serviceMeta) {
        for (ServiceMeta meta : serviceMeta) {
            registryService.unregister(meta);
            publishedServices.remove(meta);
        }
        return this;
    }

    @Override
    public ServiceProvider unpublishAllService() {
        Iterator<ServiceMeta> it = publishedServices.iterator();
        while (it.hasNext()) {
            ServiceMeta meta = it.next();
            registryService.unregister(meta);
            it.remove();
        }
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

    /**
     * Blocking current thread.
     */
    private void waitCompletePublish() {
        lock.lock();
        try {
            notComplete.await(RpcConfig.getPublishTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("blockCurrent", e);
        } finally {
            lock.unlock();
        }
        if (timeout) {
            logger.warn("publish service failed");
        } else {
            logger.info("publish service successful");
        }
        timeout = true; // reset
    }

    @Override
    public int workers() {
        return workers;
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
        executor.shutdown();
    }

    private void checkAddress(String[] address) {
        if (address == null || address.length < 1) {
            throw new IllegalStateException("invalid registry_cluster address, please check address");
        }
    }
}
