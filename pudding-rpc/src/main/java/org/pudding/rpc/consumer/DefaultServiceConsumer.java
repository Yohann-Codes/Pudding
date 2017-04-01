package org.pudding.rpc.consumer;

import org.apache.log4j.Logger;
import org.pudding.common.exception.NotConnectRegistryException;
import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.model.Services;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.consumer.config.ConsumerConfig;
import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.rpc.consumer.local_service.DefaultLocalManager;
import org.pudding.rpc.consumer.local_service.LocalManager;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyConnector;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 建议单例使用.
 *
 * @author Yohann.
 */
public class DefaultServiceConsumer implements ServiceConsumer {
    private static final Logger logger = Logger.getLogger(DefaultServiceConsumer.class);

    private Channel registryChannel; // 断开连接置为null

    private BlockingQueue<ServiceMeta> subscribeQueue;

    private LocalManager localManager; // 管理本地缓存服务

    private int serviceCount = 0;
    private boolean timeout = true;

    private Lock lock = new ReentrantLock();
    private Condition notResponse = lock.newCondition();

    public DefaultServiceConsumer() {
        subscribeQueue = new LinkedBlockingQueue<>();
        localManager = DefaultLocalManager.getLocalManager();
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
        doSubscribe(serviceClazz);
        blockCurrent(); // blocking
        return this;
    }

    @Override
    public ServiceConsumer subscribeServices(Class... serviceClazzs) {
        for (Class serivceClazz : serviceClazzs) {
            doSubscribe(serivceClazz);
        }

        blockCurrent(); // blocking
        return this;
    }

    /**
     * 阻塞当前线程.
     */
    private void blockCurrent() {
        lock.lock();
        try {
            notResponse.await(ConsumerConfig.subscribeTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        if (timeout) {
            logger.warn("Service subscription timed out");
        } else {
            logger.info("Service subscription is complete");
        }
    }

    private void doSubscribe(Class serviceClazz) {
        checkConnection();

        String serviceName = serviceClazz.getName();
        ServiceMeta serviceMeta = new ServiceMeta(serviceName, null);
        try {
            subscribeQueue.put(serviceMeta);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        serviceCount++;
    }

    private void checkConnection() {
        if (registryChannel == null) {
            throw new NotConnectRegistryException("not connect to registry");
        }
    }

    /**
     * 根据服务订阅响应后续处理.
     *
     * @param services
     * @param resultCode
     */
    public void processSubscribeResult(Services services, int resultCode) {
        if (resultCode == ProtocolHeader.SUCCESS) {
            for (ServiceMeta s : services.getServiceMetas()) {
                localManager.cacheService(s);
            }
            if ((--serviceCount) == 0) {
                timeout = false;
                lock.lock();
                notResponse.signalAll(); // 唤醒订阅线程
                lock.unlock();
            }
        }
    }

    private Connector newConnector() {
        return new NettyConnector(getProcessor());
    }

    private ConsumerProcessor getProcessor() {
        return new ConsumerProcessor(this);
    }
}
