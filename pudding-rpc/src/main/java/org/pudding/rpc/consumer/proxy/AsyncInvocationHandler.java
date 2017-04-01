package org.pudding.rpc.consumer.proxy;

import org.apache.log4j.Logger;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.consumer.load_balance.LoadBalance;
import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.transport.netty.INettyConnector;
import org.pudding.transport.netty.NettyConnector;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 异步调用处理器.
 *
 * @author Yohann.
 */
public class AsyncInvocationHandler implements InvocationHandler, InvokeHandler {
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
        String serviceName = proxy.getClass().getInterfaces()[0].getSimpleName();
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();
        InvokeMeta invokeMeta = new InvokeMeta(serviceName, methodName, paramTypes, args);

        ServiceMeta serviceMeta = loadBalance.selectService(invokeMeta);

        return null;
    }

    /**
     * 连接服务.
     *
     * @param serviceAddress
     */
    private void connect(String serviceAddress) {

    }

    @Override
    public void invokeComplete(Long invokeId, Object result) {

    }
}
