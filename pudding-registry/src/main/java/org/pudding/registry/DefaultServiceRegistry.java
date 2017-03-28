package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.registry.processor.RegistryExecutor;
import org.pudding.registry.processor.RegistryProcessor;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyAcceptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 默认注册中心实现.
 *
 * @author Yohann.
 */
public class DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final Acceptor acceptor;
    private Future future;

    public DefaultServiceRegistry() {
        acceptor = new NettyAcceptor(RegistryProcessor.PROCESSOR);
    }

    @Override
    public void startRegistry() {
        startRegistry(0);
    }

    @Override
    public void startRegistry(int port) {
        if (port == 0) {
            port = RegistryConfig.port();
        }
        startRegistry(port, 0);
    }

    @Override
    public void startRegistry(int port, int nWorkers) {
        if (nWorkers == 0) {
            nWorkers = RegistryConfig.nWorkers();
        }
        RegistryProcessor.PROCESSOR.createExecutor();
        acceptor.bind(port);
    }

    @Override
    public void closeRegistry() {
        validate(future);
        RegistryProcessor.PROCESSOR.shutdownExecutor();
        future.channel().close();
    }

    private void validate(Future future) {
        if (future == null) {
            throw new NullPointerException("future == null");
        }
    }
}