package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.exception.NotConnectRegistryException;
import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.exception.ServiceNotStartedException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.rpc.provider.processor.ProviderProcessor;
import org.pudding.rpc.provider.utils.ServiceMap;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.*;
import org.pudding.transport.netty.NettyAcceptor;
import org.pudding.transport.netty.NettyConnector;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Yohann.
 */
public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = Logger.getLogger(DefaultServiceProvider.class);

    private final int nWorkers;

    private Channel registryChannel; // 断开连接置为null

    private ServiceMap onlineServices; // 已上线服务(收到注册成功的响应)
    private ServiceMap unsettledServices; // 已发布，但还未收到响应
    private LinkedList<ServiceMeta> startedServices; // 已启用的服务(待发布服务)

    private BlockingQueue<ServiceMeta> publishQueue;

    /**
     * 默认工作线程数量: nWorkers = 2 * CPU
     */
    public DefaultServiceProvider() {
        this(ProviderConfig.nWorkers());
    }

    public DefaultServiceProvider(int nWorkers) {
        validate(nWorkers);
        this.nWorkers = nWorkers;
        onlineServices = new ServiceMap();
        unsettledServices = new ServiceMap();
        startedServices = new LinkedList<>();
        publishQueue = new LinkedBlockingQueue<>();
    }

    private void validate(int nWorkers) {
        if (nWorkers < 1) {
            throw new IllegalArgumentException("nWorker: " + nWorkers);
        }
    }

    @Override
    public ServiceProvider connectRegistry() {
        return connectRegistry(ProviderConfig.registryAddress());
    }

    @Override
    public ServiceProvider connectRegistry(String registryAddress) {
        if (registryChannel != null) {
            throw new RepeatConnectRegistryException("the registry is connected: " + registryAddress);
        }
        AddressUtil.checkFormat(registryAddress);
        String host = AddressUtil.host(registryAddress);
        int port = AddressUtil.port(registryAddress);

        doConnectRegisry(host, port);
        initPublishQueue();
        return this;
    }

    private void doConnectRegisry(String host, int port) {
        Connector connector = newConnector();
        Future future = connector.connect(host, port);
        if (future != null) {
            registryChannel = future.channel();
        }
    }

    private void initPublishQueue() {
        new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        ServiceMeta serviceMeta = publishQueue.take();
                        send(serviceMeta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.start();
    }

    @Override
    public void closeRegistry() {
        registryChannel.close();
        registryChannel = null;
    }

    @Override
    public ServiceProvider startService(ServiceMeta serviceMeta) {
        checkNotNull(serviceMeta, ServiceMeta.class);
        String address = serviceMeta.getAddress();
        AddressUtil.checkFormat(address);
        int port = AddressUtil.port(address);

        Future future = doStart(port); // bind local port

        if (future == null) {
            throw new NullPointerException("future == null");
        }

        unsettledServices.put(serviceMeta, future.channel());
        startedServices.offer(serviceMeta); // 添加到待发布队列

        return this;
    }

    private Future doStart(int port) {
        Acceptor acceptor = newAcceptor();
        return acceptor.bind(port);
    }

    @Override
    public ServiceProvider startServices(ServiceMeta... serviceMetas) {
        for (ServiceMeta serviceMeta : serviceMetas) {
            startService(serviceMeta);
        }
        return this;
    }

    @Override
    public ServiceProvider publishService(ServiceMeta serviceMeta) {
        if (!startedServices.contains(serviceMeta)) {
            throw new ServiceNotStartedException("you must start it before publish service");
        }
        return doPublish(serviceMeta);
    }

    private ServiceProvider doPublish(ServiceMeta serviceMeta) {
        checkConnection();
        checkNotNull(serviceMeta, ServiceMeta.class);

        try {
            publishQueue.put(serviceMeta);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return this;
    }

    private void send(final ServiceMeta serviceMeta) {
        // 序列化
        Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
        byte[] body = serializer.writeObject(serviceMeta);
        MessageHolder holder = MessageHolderFactory.newPublishServiceRequestHolder(body, ProviderConfig.serializerType());

        if (registryChannel.isActive()) {
            Future future = registryChannel.write(holder);
            future.addListener(new FutureListener() {
                @Override
                public void operationComplete(boolean isSuccess) {
                    if (isSuccess) {
                        startedServices.remove(serviceMeta);
                    }
                }
            });
        }
    }

    private void checkConnection() {
        if (registryChannel == null) {
            throw new NotConnectRegistryException("not connect to registry");
        }
    }

    @Override
    public ServiceProvider publishServices(ServiceMeta... serviceMetas) {
        for (ServiceMeta serviceMeta : serviceMetas) {
            publishService(serviceMeta);
        }
        return this;
    }

    @Override
    public ServiceProvider publishAllService() {
        if (startedServices.size() < 1) {
            throw new ServiceNotStartedException("you must start it before publish service");
        }
        for (; ; ) {
            ServiceMeta serviceMeta = startedServices.poll();
            if (serviceMeta == null) {
                break;
            }
            doPublish(serviceMeta);
        }
        return this;
    }

    @Override
    public ServiceProvider startAndPublishService(ServiceMeta serviceMeta) {
        startService(serviceMeta);
        publishService(serviceMeta);
        return this;
    }

    @Override
    public ServiceProvider startAndPublishServices(ServiceMeta... serviceMetas) {
        for (ServiceMeta serviceMeta : serviceMetas) {
            startAndPublishService(serviceMeta);
        }
        return this;
    }

    /**
     * 根据服务发布响应后续处理.
     *
     * @param serviceMeta
     * @param resultCode
     */
    public void processPublishResult(ServiceMeta serviceMeta, int resultCode) {
        if (resultCode == ProtocolHeader.SUCCESS) {
            onlineServices.put(serviceMeta, unsettledServices.get(serviceMeta));
            logger.info("服务发布成功: " + serviceMeta);
        } else if (resultCode == ProtocolHeader.FAILED) {
            logger.info("服务发布失败: " + serviceMeta);
        }
        unsettledServices.remove(serviceMeta);
    }

    private Connector newConnector() {
        return new NettyConnector(getProcessor());
    }

    private Acceptor newAcceptor() {
        return new NettyAcceptor(getProcessor());
    }

    private ProviderProcessor getProcessor() {
        return new ProviderProcessor(this, nWorkers);
    }

    private void checkNotNull(Object object, Class<?> clazz) {
        if (object == null) {
            throw new NullPointerException(clazz.getName());
        }
    }
}
