package org.pudding.registry;

import org.apache.log4j.Logger;
import org.pudding.registry.config.RegistryConfig;
import org.pudding.registry.processor.RegistryProcessor;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyAcceptor;

/**
 * 默认注册中心实现.
 *
 * @author Yohann.
 */
public class DefaultServiceRegistry implements ServiceRegistry {
    private static final Logger logger = Logger.getLogger(DefaultServiceRegistry.class);

    private final Acceptor acceptor;
    private RegistryProcessor processor;
    private Future future;

    public DefaultServiceRegistry() {
        this(RegistryConfig.nWorkers());
    }

    public DefaultServiceRegistry(int nWorkers) {
        validate(nWorkers);
        processor = new RegistryProcessor(this, nWorkers);
        acceptor = new NettyAcceptor(processor);
    }

    private void validate(int nWorkers) {
        if (nWorkers < 1) {
            throw new IllegalArgumentException("nWorker: " + nWorkers);
        }
    }

    @Override
    public void startRegistry() {
        startRegistry(RegistryConfig.port());
    }

    @Override
    public void startRegistry(int port) {
        acceptor.bind(port);
    }

    @Override
    public void closeRegistry() {
        validate(future);
        processor.shutdownExecutor();
        future.channel().close();
    }

    private void validate(Future future) {
        if (future == null) {
            throw new NullPointerException("future == null");
        }
    }
}