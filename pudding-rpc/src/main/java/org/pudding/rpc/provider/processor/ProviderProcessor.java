package org.pudding.rpc.provider.processor;

import org.apache.log4j.Logger;
import org.pudding.common.model.InvokeMeta;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;
import org.pudding.common.utils.MessageHolderFactory;
import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.config.ProviderConfig;
import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;

import java.lang.reflect.Method;
import java.util.Map;

import static org.pudding.common.protocol.ProtocolHeader.dataPacketCode;

/**
 * Provider Processor implementation.
 *
 * @author Yohann.
 */
public class ProviderProcessor extends ProviderExecutor implements Processor {
    private static final Logger logger = Logger.getLogger(ProviderProcessor.class);

    private DefaultServiceProvider serviceProvider;
    private final Map<String, Object> serviceInstances;

    public ProviderProcessor(DefaultServiceProvider serviceProvider, int nWorkers) {
        super(nWorkers);
        this.serviceProvider = serviceProvider;
        serviceInstances = serviceProvider.getServiceInstances();
    }

    @Override
    public void channelRead(final Channel channel, final MessageHolder holder) {

        // 由线程池执行
        execute(new Runnable() {
            @Override
            public void run() {
                // 解析MessageHolder
                ProtocolHeader header = holder.getHeader();
                byte serializationType = ProtocolHeader.serializationCode(header.getType());
                byte packetType = dataPacketCode(header.getType());
                byte sign = header.getSign();
                long invokeId = header.getInvokeId();
                int resultCode = header.getResultCode();
                byte[] body = holder.getBody();
                dispatch(channel, serializationType, packetType, sign, invokeId, resultCode, body);
            }
        });
    }

    /**
     * 消息分派.
     *
     * @param channel
     * @param serializationType
     * @param packetType
     * @param sign
     * @param invokeId
     * @param resultCode
     * @param body
     */
    private void dispatch(Channel channel, byte serializationType,
                          byte packetType, byte sign, long invokeId, int resultCode, byte[] body) {
        Serializer serializer = SerializerFactory.getSerializer(serializationType);

        switch (packetType) {
            case ProtocolHeader.REQUEST:
                switch (sign) {
                    case ProtocolHeader.INVOKE_SERVICE:
                        InvokeMeta invokeMeta = serializer.readObject(body, InvokeMeta.class);
                        processInvoke(channel, invokeMeta, invokeId);
                        break;
                }
                break;

            case ProtocolHeader.RESPONSE:
                switch (sign) {
                    case ProtocolHeader.PUBLISH_SERVICE:
                        ServiceMeta serviceMeta = serializer.readObject(body, ServiceMeta.class);
                        serviceProvider.processPublishResult(serviceMeta, resultCode);
                        break;
                }
                break;
        }
    }

    /**
     * 处理远程调用请求.
     *
     * @param channel
     * @param invokeMeta
     * @param invokeId
     */
    private void processInvoke(Channel channel, InvokeMeta invokeMeta, long invokeId) {
        MessageHolder holder;
        boolean success = true;
        Object result = null;

        String serviceName = invokeMeta.getServiceName();
        String methodName = invokeMeta.getMethodName();
        Class<?>[] paramTypes = invokeMeta.getParamTypes();
        Object[] params = invokeMeta.getParams();
        try {
            Class<?> service = Class.forName(serviceName);
            Method method = service.getMethod(methodName, paramTypes);
            if (serviceInstances.containsKey(serviceName)) {
                Object instance = serviceInstances.get(serviceName);
                result = method.invoke(instance, params);
            } else {
                // 未找到服务
                success = false;
            }
        } catch (Exception e) {
            // 未找到服务
            success = false;
        } finally {
            // 发送调用结果
            if (success) {
                Serializer serializer = SerializerFactory.getSerializer(ProviderConfig.serializerType());
                holder = MessageHolderFactory.newInvokeResponseHolder(serializer.writeObject(result),
                        ProviderConfig.serializerType(), invokeId, ProtocolHeader.SUCCESS);
            } else {
                holder = MessageHolderFactory.newInvokeResponseHolder(new byte[]{},
                        ProviderConfig.serializerType(), invokeId, ProtocolHeader.FAILED);
            }
            if (channel.isActive()) {
                channel.write(holder);
            }
        }
    }

    @Override
    public void channelActive(Channel channel) {
    }

    @Override
    public void channelInactive(Channel channel) {
    }
}
