package org.pudding.rpc.consumer.proxy;

import org.apache.log4j.Logger;
import org.pudding.common.exception.InvokeFailedException;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.utils.IdUtil;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.consumer.future.InvokeFuture;
import org.pudding.rpc.consumer.future.InvokeFutureListener;
import org.pudding.rpc.consumer.load_balance.LoadBalance;
import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.INettyConnector;
import org.pudding.transport.netty.NettyConnector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.pudding.rpc.consumer.proxy.ChannelManager.serviceMap;

/**
 * 异步调用处理器.
 *
 * @author Yohann.
 */
public class AsyncInvocationHandler extends InvokeFuture implements InvocationHandler, InvokeHandler {
    private static final Logger logger = Logger.getLogger(AsyncInvocationHandler.class);

    private final LoadBalance loadBalance;
    private final INettyConnector connector;

    public AsyncInvocationHandler() {
        loadBalance = new LoadBalance();
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

        doInvoke(channel, invokeMeta);

        return result(method.getReturnType());
    }

    /**
     * 根据类型返回默认值.
     *
     * @param returnType
     * @return
     */
    private Object result(Class<?> returnType) {
        String name = returnType.getName();
        Object result;
        switch (name) {
            case "boolean":
                result = InitValue.BOOLEAN_VALUE;
                break;
            case "byte":
                result = InitValue.BYTE_VALUE;
                break;
            case "short":
                result = InitValue.SHORT_VALUE;
                break;
            case "int":
                result = InitValue.INT_VALUE;
                break;
            case "long":
                result = InitValue.LONG_VALUE;
                break;
            case "float":
                result = InitValue.FLOAT_VALUE;
                break;
            case "double":
                result = InitValue.DOUBLE_VALUE;
                break;

            default:
                result = null;
        }
        return result;
    }

    /**
     * 发起远程调用
     *
     * @param channel
     * @param invokeMeta
     */
    private void doInvoke(Channel channel, InvokeMeta invokeMeta) {
        Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
        byte[] body = serializer.writeObject(invokeMeta);

        long invokeId = IdUtil.invokeId();
        InvokeFuture.invokeId = invokeId;

        MessageHolder holder = MessageHolderFactory.newInvokeRequestHolder(
                body, ProviderConfig.serializerType(), invokeId);

        if (channel.isActive()) {
            channel.write(holder);
        }
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

    @Override
    public void invokeComplete(long invokeId, Object result, int resultCode) {

        for (int i = 0; i < 50; i++) {
            if (INVOKE_MAP.containsKey(invokeId)) {
                break;
            }
        }

        if (INVOKE_MAP.containsKey(invokeId)) {
            InvokeFutureListener<Object> listener = INVOKE_MAP.get(invokeId);
            if (resultCode == ProtocolHeader.SUCCESS) {
                listener.success(result);
            } else {
                listener.failure(new InvokeFailedException("invoke failed"));
            }
        }
    }

    /**
     * 基本类型初始值.
     */
    private class InitValue {
        public static final boolean BOOLEAN_VALUE = false;
        public static final byte BYTE_VALUE = 0;
        public static final short SHORT_VALUE = 0;
        public static final int INT_VALUE = 0;
        public static final float FLOAT_VALUE = (float) 0.0;
        public static final long LONG_VALUE = 0;
        public static final double DOUBLE_VALUE = 0.0;
    }
}
