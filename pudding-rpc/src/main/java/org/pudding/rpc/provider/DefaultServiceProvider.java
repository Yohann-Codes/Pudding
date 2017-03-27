package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.exception.NotConnectRegistryException;
import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.exception.ServiceNotPublishedException;
import org.pudding.common.utils.AddressUtil;
import org.pudding.common.model.ServiceMeta;
import org.pudding.rpc.processor.ProviderProcessor;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Connector;
import org.pudding.transport.api.Future;
import org.pudding.transport.netty.NettyAcceptor;
import org.pudding.transport.netty.NettyConnector;

/**
 * 默认的服务提供者实现.
 *
 * @author Yohann.
 */
public class DefaultServiceProvider implements ServiceProvider {
    private static final Logger logger = Logger.getLogger(DefaultServiceProvider.class);

    private Channel channel;
    private Connector connector;
    private Acceptor acceptor;

    private ServiceMeta serviceMeta;

    public DefaultServiceProvider() {
        connector = new NettyConnector(ProviderProcessor.PROCESSOR);
        acceptor = new NettyAcceptor(ProviderProcessor.PROCESSOR);
    }

    @Override
    public ServiceProvider connectRegistry(String registryAddress) {
        if (channel != null) {
            throw new RepeatConnectRegistryException("Registry has connected: " + registryAddress);
        }
        AddressUtil.checkFormat(registryAddress);
        String host = AddressUtil.host(registryAddress);
        int port = AddressUtil.port(registryAddress);
        doConnectRegisry(host, port);
        return this;
    }

    private void doConnectRegisry(String host, int port) {
        connector = new NettyConnector(new ProviderProcessor());
        Future future = connector.connect(host, port);
        channel = future.channel();
    }

    @Override
    public ServiceProvider publishService(ServiceMeta serviceMeta) {
        checkConnection();
        validate(serviceMeta);
        this.serviceMeta = serviceMeta;

        return this;
    }

    private void checkConnection() {
        if (channel == null) {
            throw new NotConnectRegistryException("Not connect to Registry");
        }
    }

    @Override
    public ServiceProvider publishServices(ServiceMeta... serviceMetas) {
        for (ServiceMeta s : serviceMetas) {
            publishService(s);
        }
        return this;
    }

    @Override
    public ServiceProvider startService() {
        if (serviceMeta == null) {
            throw new ServiceNotPublishedException("Please publish it before start the service");
        }
        String address = serviceMeta.getAddress();
        AddressUtil.checkFormat(address);
        int port = AddressUtil.port(address);
        doStart(port);
        serviceMeta = null;
        return this;
    }

    private void doStart(int port) {
        acceptor.bind(port);
    }

    @Override
    public ServiceProvider publishAndStartService(ServiceMeta serviceMeta) {
        publishService(serviceMeta);
        startService();
        return this;
    }

    @Override
    public ServiceProvider publishAndStartServices(ServiceMeta... serviceMetas) {
        for (ServiceMeta s : serviceMetas) {
            publishAndStartService(s);
        }
        return this;
    }

    private void validate(ServiceMeta serviceMeta) {
        if (serviceMeta == null) {
            throw new NullPointerException("service == null");
        }
    }
}