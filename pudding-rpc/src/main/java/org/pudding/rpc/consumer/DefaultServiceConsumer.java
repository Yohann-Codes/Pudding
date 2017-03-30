package org.pudding.rpc.consumer;

import org.apache.log4j.Logger;
import org.pudding.common.exception.NotConnectRegistryException;
import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.SubscribeResult;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.consumer.future.ConsumerFuture;
import org.pudding.rpc.consumer.future.SubscribeFutureListener;
import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.rpc.consumer.service.DefaultLocalManager;
import org.pudding.rpc.consumer.service.LocalManager;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyConnector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 默认的服务消费者.
 *
 * @author Yohann.
 */
public class DefaultServiceConsumer implements ServiceConsumer {
    private static final Logger logger = Logger.getLogger(DefaultServiceConsumer.class);

    private Channel registryChannel; // 断开连接置为null

    private ConsumerFuture<SubscribeFutureListener> consumerFuture;

    private BlockingQueue<ServiceMeta> subscribeQueue;

    private LocalManager localManager; // 管理本地缓存服务

    public DefaultServiceConsumer() {
        consumerFuture = new ConsumerFuture<>();
        subscribeQueue = new LinkedBlockingQueue<>();
        localManager = new DefaultLocalManager();
    }

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
        initSubscribeQueue();
        return this;
    }

    private void doConnectRegisry(String host, int port) {
        Connector connector = newConnector();
        Future future = connector.connect(host, port);
        if (future != null) {
            registryChannel = future.channel();
        }
    }

    private void initSubscribeQueue() {
        new Thread() {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        ServiceMeta serviceMeta = subscribeQueue.take();
                        send(serviceMeta);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }.start();
    }

    private void send(final ServiceMeta serviceMeta) {
        // 序列化
        Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
        byte[] body = serializer.writeObject(serviceMeta);
        MessageHolder holder = MessageHolderFactory.newSubscribeServiceRequestHolder(body, ProviderConfig.serializerType());

        if (registryChannel.isActive()) {
            registryChannel.write(holder);
        }
    }

    @Override
    public void closeRegistry() {
        registryChannel.close();
        registryChannel = null;
    }

    @Override
    public ServiceConsumer subscribeService(Class serviceClazz) {
        String serviceName = serviceClazz.getSimpleName();
        ServiceMeta serviceMeta = new ServiceMeta(serviceName, null);

        doSubscribe(serviceMeta);
        return this;
    }

    private void doSubscribe(ServiceMeta serviceMeta) {
        checkConnection();
        try {
            subscribeQueue.put(serviceMeta);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkConnection() {
        if (registryChannel == null) {
            throw new NotConnectRegistryException("not connect to registry");
        }
    }

    @Override
    public ServiceConsumer subscribeServices(Class... serviceClazzs) {
        for (Class serivceClazz : serviceClazzs) {
            subscribeService(serivceClazz);
        }
        return this;
    }

    @Override
    public void addSubscribeFutureListener(SubscribeFutureListener listener) {
        checkNotNull(listener);
        consumerFuture.addFutureListener(listener);
    }

    private void checkNotNull(SubscribeFutureListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
    }

    /**
     * 根据服务订阅响应后续处理.
     *
     * @param subscribeResult
     * @param resultCode
     */
    public void processSubscribeResult(SubscribeResult subscribeResult, int resultCode) {
        SubscribeFutureListener listener = consumerFuture.getListener();
        if (listener != null) {
            boolean isSuccess = false;
            List<String> address = new ArrayList<>();
            if (resultCode == ProtocolHeader.SUCCESS) {
                isSuccess = true;
                for (ServiceMeta s : subscribeResult.getServiceMetas()) {
                    localManager.cacheService(s);
                    address.add(s.getAddress());
                }
            }
            listener.suscribeComplete(subscribeResult.getName(), address, isSuccess);
        } else {// 表明没有调用addSubscribeFutureListener()添加监听...
        }
    }

    private Connector newConnector() {
        return new NettyConnector(getProcessor());
    }

    private ConsumerProcessor getProcessor() {
        return new ConsumerProcessor(this);
    }

}
