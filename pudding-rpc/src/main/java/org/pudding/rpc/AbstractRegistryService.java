package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstract registry service.
 *
 * @author Yohann.
 */
public abstract class AbstractRegistryService implements RegistryService {
    private static final Logger logger = Logger.getLogger(AbstractRegistryService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<ServiceMeta> taskQueue = new LinkedBlockingQueue<>(1024);

    // Control taskQueue
    private volatile boolean isShutdown = false;

    public AbstractRegistryService() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (!isShutdown) {
                    ServiceMeta meta;
                    try {
                        meta = taskQueue.take();
                        doRegister(meta);
                    } catch (InterruptedException e) {
                        logger.warn("take from taskQueue");
                    }
                }
            }
        });
    }

    @Override
    public void register(ServiceMeta serviceMeta) {
        try {
            taskQueue.put(serviceMeta);
        } catch (InterruptedException e) {
            logger.warn("put service meta to taskQueue: " + serviceMeta);
        }
    }

    @Override
    public void unregister(ServiceMeta serviceMeta) {

    }

    @Override
    public void subscribe(ServiceMeta serviceMeta) {

    }

    @Override
    public void shutdown() {
        isShutdown = true;
        executor.shutdownNow();
    }

    protected void checkNotShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("the instance has shutdown");
        }
    }

    protected abstract void doRegister(ServiceMeta serviceMeta);

    protected abstract void doUnregister(ServiceMeta serviceMeta);

    protected abstract void doSubscribe(ServiceMeta serviceMeta);
}
