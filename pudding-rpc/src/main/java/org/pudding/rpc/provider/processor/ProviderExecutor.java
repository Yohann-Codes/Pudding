package org.pudding.rpc.provider.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provider Executor.
 * 处理非IO事件.
 *
 * @author Yohann.
 */
public class ProviderExecutor {
    private ExecutorService executor;

    public ProviderExecutor(int nWorkers) {
        executor = Executors.newFixedThreadPool(nWorkers);
    }

    protected void execute(Runnable task) {
        executor.execute(task);
    }

    public void shutdownExecutor() {
        executor.shutdown();
    }
}
