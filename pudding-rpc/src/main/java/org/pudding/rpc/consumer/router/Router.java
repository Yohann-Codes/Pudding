package org.pudding.rpc.consumer.router;

import org.apache.log4j.Logger;
import org.pudding.common.constant.LoadBalanceStrategy;
import org.pudding.common.exception.NotFindServiceException;
import org.pudding.common.model.ServiceMeta;
import org.pudding.common.utils.AddressUtil;
import org.pudding.rpc.RpcConfig;
import org.pudding.rpc.consumer.AcknowledgeManager;
import org.pudding.rpc.consumer.load_balance.*;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.netty.NettyTransportFactory;

/**
 * Select a {@link Channel}.
 *
 * @author Yohann.
 */
public abstract class Router extends AcknowledgeManager {
    private static final Logger logger = Logger.getLogger(Router.class);

    private final Connector connector = NettyTransportFactory.createTcpConnector();

    private LoadBalancer loadBalancer;

    public Router(LoadBalanceStrategy loadBalanceStrategy) {
        initLoadBalancer(loadBalanceStrategy);
        initConnector(connector);
    }

    protected abstract void initConnector(Connector connector);

    private void initLoadBalancer(LoadBalanceStrategy loadBalanceStrategy) {
        switch (loadBalanceStrategy) {
            case RANDOM:
                loadBalancer = new RandomLoadBalancer();
                break;
            case ROUND:
                loadBalancer = new RoundLoadBalancer();
                break;
            case WEIGHTED_RADOM:
                loadBalancer = new WeightRandomBalancer();
                break;
            case WEIGHTED_ROUND:
                loadBalancer = new WeightRoundBalancer();
                break;
        }
    }

    /**
     * Get the route of invoking.
     */
    protected InvocationPair route(String serviceName) {
        // First: select a ServiceMeta from LocalServiceContainer
        // Second: select a channel from RouteMap

        ServiceMeta serviceMeta;
        serviceMeta = loadBalancer.select(serviceName);
        if (serviceMeta == null) {
            throw new NotFindServiceException("Not find service");
        }

        Channel channel = RouteMap.get(serviceMeta);
        Object serviceInstance = serviceMeta.getInstance();

        if (channel == null) {
            // connect with provider
            String address = serviceMeta.getAddress();
            String host = AddressUtil.host(address);
            int port = AddressUtil.port(address);
            try {
                channel = connector.connect(host, port);
                RouteMap.put(serviceMeta, channel);
            } catch (InterruptedException e) {
                connector.shutdownGracefully();
                logger.warn("connect", e);
            }
        }

        return new InvocationPair(channel, serviceInstance);
    }

    protected class InvocationPair {
        private Channel channel;
        private Object serviceInstance;

        public InvocationPair(Channel channel, Object serviceInstance) {
            this.channel = channel;
            this.serviceInstance = serviceInstance;
        }

        public Channel getChannel() {
            return channel;
        }

        public Object getServiceInstance() {
            return serviceInstance;
        }
    }
}
