package org.pudding.rpc;

import org.apache.log4j.Logger;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.Message;
import org.pudding.common.utils.RandomUtil;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Processor;
import org.pudding.transport.netty.NettyTcpConnector;

import java.net.SocketAddress;

/**
 * The default implementation of {@link RegistryService}.
 *
 * @author Yohann.
 */
public class DefaultRegistryService extends AbstractRegistryService {
    private static final Logger logger = Logger.getLogger(DefaultRegistryService.class);

    public static final Processor PROCESSOR = new RegistryServiceProcessor();

    private final Connector connector = new NettyTcpConnector();

    // Registry channel
    private Channel channel;
    private volatile boolean isConnected = false;

    @Override
    public void connectRegistry(SocketAddress... address) {
        int size = address.length;
        SocketAddress selectedAddress;

        if (size < 2) {
            selectedAddress = address[0];
        } else {
            // Select an address randomly
            int index = RandomUtil.getInt(size);
            selectedAddress = address[index];
        }

        synchronized (this) {
            if (isConnected) {
                throw new IllegalStateException("has connected to registry: " + channel);
            }
            try {
                // Connect to registry
                channel = connector.connect(selectedAddress);
                isConnected = true;
            } catch (InterruptedException e) {
                logger.warn("connect to registry failed: " + selectedAddress);
            }
        }
    }

    @Override
    public void disconnectRegistry() {
        synchronized (this) {
            if (!isConnected) {
                throw new IllegalStateException("not connect to registry");
            }
            channel.close();
            channel = null;
            isConnected = false;
        }
    }

    @Override
    protected void doRegister(ServiceMeta serviceMeta) {

    }

    @Override
    protected void doUnregister(ServiceMeta serviceMeta) {

    }

    @Override
    protected void doSubscribe(ServiceMeta serviceMeta) {

    }

    private static class RegistryServiceProcessor implements Processor {

        @Override
        public void handleMessage(Channel channel, Message holder) {

        }
    }
}
