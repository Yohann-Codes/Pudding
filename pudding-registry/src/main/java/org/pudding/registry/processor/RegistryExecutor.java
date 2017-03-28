package org.pudding.registry.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yohann.
 */
public class RegistryExecutor {
    private int nWorkers;
    private ExecutorService executor;

    public RegistryExecutor(int nWorkers) {
        this.nWorkers = nWorkers;
    }

    public void createExecutor() {
        executor = Executors.newFixedThreadPool(nWorkers);
    }

    public void shutdownExecutor() {
        executor.shutdown();
    }

    protected void execute(Runnable task) {
        executor.execute(task);
    }


}
