package org.pudding.registry.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Registry Executor.
 * 处理非IO事件.
 *
 * @author Yohann.
 */
public class RegistryExecutor {
    private ExecutorService executor;

    public RegistryExecutor(int nWorkers) {
        executor = Executors.newFixedThreadPool(nWorkers);
    }

    protected void execute(Runnable task) {
        executor.execute(task);
    }

    public void shutdownExecutor() {
        executor.shutdown();
    }
}
