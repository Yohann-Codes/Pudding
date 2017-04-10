package org.pudding.transport.netty.connection;

import org.apache.log4j.Logger;
import org.pudding.common.utils.Maps;
import org.pudding.transport.api.ConnectionManager;

import java.util.concurrent.ConcurrentMap;

/**
 * The connection manager of Netty.
 *
 * @author Yohann.
 */
public class NettyConnectionManager implements ConnectionManager {
    private static final Logger logger = Logger.getLogger(NettyConnectionManager.class);

    private final ConcurrentMap<String, ConnectionWatchdog> connections = Maps.newConcurrentHashMap();



    @Override
    public void openAutoRennection(String address) {
    }

    @Override
    public void closeAutoRennection(String address) {

    }
}
