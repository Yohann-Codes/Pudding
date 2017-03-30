package org.pudding.rpc.consumer.processor;

import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.utils.AddressUtil;
import org.pudding.rpc.consumer.ServiceConsumer;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyConnector;

/**
 * 默认的服务消费者.
 *
 * @author Yohann.
 */
public class DefaultServiceConsumer implements ServiceConsumer {

    private Channel registryChannel; // 断开连接置为null

    @Override
    public ServiceConsumer connectRegistry() {
        return connectRegistry(ProviderConfig.registryAddress());

    }

    @Override
    public ServiceConsumer connectRegistry(String registryAddress) {
        if (registryChannel != null) {
            throw new RepeatConnectRegistryException("the registry is connected: " + registryAddress);
        }
        AddressUtil.checkFormat(registryAddress);
        String host = AddressUtil.host(registryAddress);
        int port = AddressUtil.port(registryAddress);

        doConnectRegisry(host, port);
        return this;
    }

    private void doConnectRegisry(String host, int port) {
        Connector connector = newConnector();
        Future future = connector.connect(host, port);
        if (future != null) {
            registryChannel = future.channel();
        }
    }

    @Override
    public void closeRegistry() {
        registryChannel.close();
        registryChannel = null;
    }

    @Override
    public ServiceConsumer subscribeService(Class serviceClazz) {
        return null;
    }

    @Override
    public ServiceConsumer subscribeServices(Class... serviceClazzs) {
        return null;
    }

    private Connector newConnector() {
        return new NettyConnector(getProcessor());
    }

    private ConsumerProcessor getProcessor() {
        return new ConsumerProcessor();
    }

}
