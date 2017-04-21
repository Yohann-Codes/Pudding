package org.pudding.rpc.provider.flow_control;

import org.pudding.rpc.RpcConfig;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Flow controller.
 * <p>
 * Control the times of service in unit time(a minute).
 *
 * @author Yohann.
 */
public class FlowController extends TimerTask {

    private volatile int actualFlow;
    private int flowThreshold;

    public FlowController() {
        flowThreshold = RpcConfig.getFlowThresold();
        new Timer().schedule(this, 0, TimeUnit.SECONDS.toMillis(60));
    }

    @Override
    public void run() {
        actualFlow = 0;
    }

    public boolean overFlowThreshold() {
        synchronized (this) {
            if (actualFlow >= flowThreshold) {
                return true;
            }
            actualFlow++;
            return false;
        }
    }
}
