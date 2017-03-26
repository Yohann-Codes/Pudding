package org.pudding.rpc.provider;

import org.apache.log4j.Logger;
import org.pudding.common.exception.NotConnectRegistryException;
import org.pudding.common.exception.RepeatConnectRegistryException;
import org.pudding.common.exception.ServiceNotPublishedException;
import org.pudding.common.utils.AddressUtil;
import org.pudding.rpc.model.Service;
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

    private Service service;

    public DefaultServiceProvider() {
        connector = new NettyConnector(ProviderProcessor.PROCESSOR);
        acceptor = new NettyAcceptor(ProviderProcessor.PROCESSOR);
    }

    @Override
    public ServiceProvider connectRegistry(String registryAddress) throws RepeatConnectRegistryException {
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
    public ServiceProvider publishService(Service service) throws NotConnectRegistryException {
        validate(service);
        this.service = service;
        return null;
    }

    @Override
    public ServiceProvider publishServices(Service... services) throws NotConnectRegistryException {
        for (Service s : services) {
            publishService(s);
        }
        return this;
    }

    @Override
    public ServiceProvider startService() throws ServiceNotPublishedException {
        if (service == null) {
            throw new ServiceNotPublishedException("Please publish it before start the service");
        }
        String address = service.getAddress();
        AddressUtil.checkFormat(address);
        int port = AddressUtil.port(address);
        doStart(port);
        service = null;
        return this;
    }

    private void doStart(int port) {
        acceptor.bind(port);
    }

    @Override
    public ServiceProvider publishAndStartService(Service service) throws NotConnectRegistryException {
        publishService(service);
        startService();
        return this;
    }

    @Override
    public ServiceProvider publishAndStartServices(Service... services) throws NotConnectRegistryException {
        for (Service s : services) {
            publishAndStartService(s);
        }
        return this;
    }

    private void validate(Service service) {
        if (service == null) {
            throw new NullPointerException("service == null");
        }
    }
}