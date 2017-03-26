package org.pudding.rpc.processor;

import org.pudding.common.protocol.MessageHolder;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;

/**
 * Provider Processor implementation.
 *
 * @author Yohann.
 */
public class ProviderProcessor implements Processor {

    public static final Processor PROCESSOR = new ProviderProcessor();

    @Override
    public void channelRead(Channel channel, MessageHolder holder) {

    }

    @Override
    public void channelActive(Channel channel) {

    }

    @Override
    public void channelInactive(Channel channel) {

    }
}
