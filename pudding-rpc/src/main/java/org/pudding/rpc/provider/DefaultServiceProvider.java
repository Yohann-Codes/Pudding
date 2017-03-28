package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.exception.*;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.provider.processor.ProviderProcessor;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.*;
import org.pudding.transport.netty.NettyAcceptor;
import org.pudding.transport.netty.NettyConnector;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的服务提供者实现，建议单例使用.
 *
 * @author Yohann.
 */
public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = Logger.getLogger(DefaultServiceProvider.class);

    private Channel registryChannel;
    private Connector connector;
    private Acceptor acceptor;

    private ServiceMeta serviceMeta;

    private Map<ServiceMeta, Channel> services; // 保存已发布并启动的服务，用于取消发布和停止服务.
    private Future future;

    public DefaultServiceProvider() {
        connector = new NettyConnector(ProviderProcessor.PROCESSOR);
        acceptor = new NettyAcceptor(ProviderProcessor.PROCESSOR);
        services = new HashMap<>();
    }

    @Override
    public ServiceProvider connectRegistry() {
        String registryAddress = ProviderConfig.registryAddress();
        connectRegistry(registryAddress);
        return this;
    }

    @Override
    public ServiceProvider connectRegistry(String registryAddress) {
        if (registryChannel != null) {
            throw new RepeatConnectRegistryException("Registry has connected: " + registryAddress);
        }
        AddressUtil.checkFormat(registryAddress);
        String host = AddressUtil.host(registryAddress);
        int port = AddressUtil.port(registryAddress);

        doConnectRegisry(host, port);
        return this;
    }

    @Override
    public void closeRegistry() {
        registryChannel.close();
        registryChannel = null;
    }

    private void doConnectRegisry(String host, int port) {
        connector = new NettyConnector(new ProviderProcessor());
        Future future = connector.connect(host, port);
        if (future != null) {
            registryChannel = future.channel();
        }
    }

    @Override
    public ServiceProvider publishService(final ServiceMeta serviceMeta) throws ServicePublishFailedException {
        checkConnection();
        validate(serviceMeta);
        this.serviceMeta = serviceMeta;

        // 序列化
        final Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
        byte[] body = serializer.writeObject(serviceMeta);
        MessageHolder holder = MessageHolderFactory.newPublishServiceRequestHolder(body, ProviderConfig.serializerType());

        if (registryChannel.isActive()) {
            Future future = registryChannel.write(holder);
            future.addListener(new FutureListener() {
                @Override
                public void operationComplete(boolean isSuccess) {
                    if (!isSuccess) {
                        logger.info("Publish service failed: " + serviceMeta);
                    }
                }
            });
        } else {
            throw new ServicePublishFailedException("Connection inactive");
        }
        return this;
    }

    private void checkConnection() {
        if (registryChannel == null) {
            throw new NotConnectRegistryException("Not connect to Registry");
        }
    }

    @Override
    public ServiceProvider publishServices(ServiceMeta... serviceMetas) throws ServicePublishFailedException {
        for (ServiceMeta s : serviceMetas) {
            publishService(s);
        }
        return this;
    }

    @Override
    public ServiceProvider startService() throws ServiceStartFailedException {
        if (serviceMeta == null) {
            throw new ServiceNotPublishedException("Please publish it before start the service");
        }
        String address = serviceMeta.getAddress();
        AddressUtil.checkFormat(address);
        int port = AddressUtil.port(address);

        doStart(port); // bind local port

        if (future == null) {
            throw new ServiceStartFailedException("future == null");
        }
        services.put(serviceMeta, future.channel());
        serviceMeta = null;
        return this;
    }

    private void doStart(int port) {
        future = acceptor.bind(port);
    }

    @Override
    public ServiceProvider publishAndStartService(ServiceMeta serviceMeta)
            throws ServicePublishFailedException, ServiceStartFailedException {

        publishService(serviceMeta);
        startService();
        return this;
    }

    @Override
    public ServiceProvider publishAndStartServices(ServiceMeta... serviceMetas)
            throws ServiceStartFailedException, ServicePublishFailedException {

        for (ServiceMeta s : serviceMetas) {
            publishAndStartService(s);
        }
        return this;
    }

    @Override
    public ServiceProvider unpublishService(final ServiceMeta serviceMeta) {
        checkConnection();
        this.serviceMeta = serviceMeta;

        // 序列化
        final Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
        byte[] body = serializer.writeObject(serviceMeta);
        MessageHolder holder = MessageHolderFactory.newUnpublishServiceRequestHolder(body, ProviderConfig.serializerType());

        if (registryChannel.isActive()) {
            Future future = registryChannel.write(holder);
            future.addListener(new FutureListener() {
                @Override
                public void operationComplete(boolean isSuccess) {
                    if (!isSuccess) {
                        logger.info("Unpublishing service failure: " + serviceMeta);
                    }
                }
            });
        }
        return this;
    }

    @Override
    public ServiceProvider stopService(ServiceMeta serviceMeta) {
        Channel serviceChannel = services.get(serviceMeta);
        serviceChannel.close(); // 关闭已绑定的Channel
        return this;
    }

    @Override
    public ServiceProvider unpublishAndStopService(ServiceMeta serviceMeta) {
        validate(serviceMeta);
        if (!services.containsKey(serviceMeta)) {
            throw new ServiceNotStartedException("The service did not start: " + serviceMeta);
        }
        // 取消并停止
        unpublishService(serviceMeta);
        stopService(serviceMeta);

        services.remove(serviceMeta);
        return this;
    }

    @Override
    public ServiceProvider unpublishAndStopAll() {
        for (Map.Entry<ServiceMeta, Channel> entry : services.entrySet()) {
            ServiceMeta serviceMeta = entry.getKey();
            unpublishAndStopService(serviceMeta);
            services.remove(serviceMeta);
        }
        return this;
    }

    private void validate(ServiceMeta serviceMeta) {
        if (serviceMeta == null) {
            throw new NullPointerException("service == null");
        }
    }
}