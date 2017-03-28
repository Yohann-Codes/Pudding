package org.pudding.example.transport;

import org.pudding.rpc.provider.processor.ProviderProcessor;
import org.pudding.transport.api.Acceptor;
import org.pudding.transport.netty.NettyAcceptor;

/**
 * @author Yohann.
 */
public class AcceptorTest {
    public static void main(String[] args) {
        Acceptor acceptor = new NettyAcceptor(ProviderProcessor.PROCESSOR);
        acceptor.bind(20000);
    }
}
