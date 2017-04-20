package org.pudding.rpc.consumer;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.Lists;
import org.pudding.rpc.DefaultRegistryService;
import org.pudding.rpc.RegistryService;
import org.pudding.rpc.RpcConfig;
import org.pudding.rpc.consumer.invoker.ServiceProxyFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The default implementation of {@link ServiceConsumer}.
 * <p>
 * Suggest use singleton.
 *
 * @author Yohann.
 */
public class DefaultServiceConsumer extends ServiceProxyFactory implements ServiceConsumer {
    private static final Logger logger = Logger.getLogger(DefaultServiceConsumer.class);

    private RegistryService registryService;

    private int workers;
    private final ExecutorService executor;

    public final Lock lock = new ReentrantLock();
    public final Condition notComplete = lock.newCondition();
    public volatile boolean timeout = true;

    public DefaultServiceConsumer() {
        this(RpcConfig.getWorkers());
    }

    public DefaultServiceConsumer(int workers) {
        validate(workers);
        this.workers = workers;
        executor = Executors.newFixedThreadPool(workers);
        ServiceProxyFactory.executor = executor;
        initService();
    }

    private void initService() {
        registryService = new DefaultRegistryService(this, executor);
    }

    private void validate(int workers) {
        if (workers < 1) {
            throw new IllegalArgumentException("workers: " + workers);
        }
    }

    @Override
    public ServiceConsumer connectRegistry() {
        String[] stringAddress = RpcConfig.getRegistryAddress();
        checkAddress(stringAddress);
        SocketAddress[] address = parseToSocketAddress(stringAddress);
        registryService.connectRegistry(address);
        return this;
    }

    @Override
    public ServiceConsumer connectRegistry(String... registryAddress) {
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
    public ServiceConsumer subscribeService(ServiceMeta serviceMeta) {
        registryService.subscribe(serviceMeta);
        waitCompletePublish();
        return this;
    }

    @Override
    public ServiceConsumer subscribeServices(ServiceMeta... serviceMeta) {
        for (ServiceMeta meta : serviceMeta) {
            registryService.subscribe(meta);
        }
        waitCompletePublish();
        return this;
    }

    /**
     * Blocking current thread.
     */
    private void waitCompletePublish() {
        lock.lock();
        try {
            notComplete.await(RpcConfig.getSubscribeTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("blockCurrent", e);
        } finally {
            lock.unlock();
        }
        if (timeout) {
            logger.warn("subscribe service failed");
        } else {
            logger.info("subscribe service successful");
        }
        timeout = true; // reset
    }

    @Override
    public int workers() {
        return workers;
    }

    @Override
    public void shutdown() {
        registryService.shutdown();
        executor.shutdown();
    }

    private void checkAddress(String[] address) {
        if (address == null || address.length < 1) {
            throw new IllegalStateException("invalid registry_cluster address, please check address");
        }
    }
}
