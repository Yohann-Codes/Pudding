package org.pudding.rpc.consumer.proxy;

import org.apache.log4j.Logger;
import org.pudding.common.exception.InvokeTimeoutException;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.IdUtil;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.consumer.config.ConsumerConfig;
import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.rpc.consumer.load_balance.LoadBalance;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.rpc.utils.ResultMap;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.INettyConnector;
import org.pudding.transport.netty.NettyConnector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.pudding.rpc.consumer.proxy.ChannelManager.serviceMap;

/**
 * 同步调用处理器.
 *
 * @author Yohann.
 */
public class SyncInvocationHandler implements InvocationHandler, InvokeHandler {
    private static final Logger logger = Logger.getLogger(SyncInvocationHandler.class);

    private final LoadBalance loadBalance;
    private final INettyConnector connector;

    // key: invokeId   value: return
    private final ResultMap results;

    private int timeout; // 服务调用超时时间

    private Lock lock = new ReentrantLock();
    private Condition notReturn = lock.newCondition();
    private boolean isTimeout = true;

    public SyncInvocationHandler(int timeout) {
        this.timeout = timeout;
        loadBalance = new LoadBalance();
        results = new ResultMap();
        connector = new NettyConnector(new ConsumerProcessor(this));
    }

    /**
     * 在此方法中封装被调方法的信息.
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serviceName = proxy.getClass().getInterfaces()[0].getName();
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();
        InvokeMeta invokeMeta = new InvokeMeta(serviceName, methodName, paramTypes, args);

        ServiceMeta serviceMeta = loadBalance.selectService(invokeMeta);

        Channel channel;
        if (serviceMap.containsKey(serviceMeta)) {
            // 与服务主机已建立连接
            channel = serviceMap.get(serviceMeta);
            // 检验连接是否可用
            if (!channel.isActive()) {
                channel = connect(serviceMeta.getAddress()); // 连接失效，重新连接
                serviceMap.put(serviceMeta, channel);
            }
        } else {
            // 与服务主机尚未建立连接
            channel = connect(serviceMeta.getAddress());
            serviceMap.put(serviceMeta, channel);
        }

        return doInvoke(channel, invokeMeta);
    }

    /**
     * 发起远程调用
     * @param channel
     * @param invokeMeta
     */
    private Object doInvoke(Channel channel, InvokeMeta invokeMeta) throws InvokeTimeoutException {
        Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
        byte[] body = serializer.writeObject(invokeMeta);

        long invokeId = IdUtil.invokeId();
        results.put(invokeId, null); // 保存调用Id

        MessageHolder holder = MessageHolderFactory.newInvokeRequestHolder(
                body, ProviderConfig.serializerType(), invokeId);

        if (channel.isActive()) {
            channel.write(holder);
        }

        blockCurrent(); // blocking

        @SuppressWarnings("unchecked")
        Object result = results.get(invokeId);
        return result;
    }

    /**
     * 阻塞当前线程.
     */
    private void blockCurrent() throws InvokeTimeoutException {
        lock.lock();
        try {
            notReturn.await(ConsumerConfig.invokeTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        if (isTimeout) {
            throw new InvokeTimeoutException("invoke timed out");
        }
    }

    @Override
    public void invokeComplete(Long invokeId, Object result) {
        // 保存调用结果
        results.put(invokeId, result);

        // 唤醒调用线程
        isTimeout = false;
        lock.lock();
        notReturn.signalAll();
        lock.unlock();
    }

    /**
     * 连接服务.
     *
     * @param serviceAddress
     */
    private Channel connect(String serviceAddress) {
        String host = AddressUtil.host(serviceAddress);
        int port = AddressUtil.port(serviceAddress);
        Future future = connector.connect(host, port);
        return future.channel();
    }
}
