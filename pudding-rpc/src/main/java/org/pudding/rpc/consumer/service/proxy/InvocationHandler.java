package org.pudding.rpc.consumer.service.proxy;

import org.apache.log4j.Logger;
import org.pudding.common.model.InvokeMeta;
import org.pudding.rpc.consumer.processor.ConsumerProcessor;
import org.pudding.rpc.consumer.load_balance.LoadBalance;
import org.pudding.transport.netty.INettyConnector;
import org.pudding.transport.netty.NettyConnector;

import java.lang.reflect.Method;

/**
 * @author Yohann.
 */
public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private static final Logger logger = Logger.getLogger(InvocationHandler.class);

    private final LoadBalance loadBalance;
    private final INettyConnector connector;

    public InvocationHandler() {
        loadBalance = new LoadBalance();
        connector = new NettyConnector(new ConsumerProcessor());
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
        Class<?>[] params = method.getParameterTypes();
        InvokeMeta invokeMeta = new InvokeMeta(serviceName, methodName, params);

        // --------------------------------------------------------------------
        logger.info("invoke(): " + invokeMeta);
        // --------------------------------------------------------------------

        String serviceAddress = loadBalance.selectServiceAddress(invokeMeta);

        return null;
    }

    /**
     * 连接服务.
     *
     * @param serviceAddress
     */
    private void connect(String serviceAddress) {

    }
}
