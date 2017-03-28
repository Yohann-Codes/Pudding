package org.pudding.rpc.consumer.processor;

import org.pudding.common.protocol.MessageHolder;
import org.pudding.transport.api.Channel;
import org.pudding.transport.api.Processor;

/**
 * Consumer Processor implementation.
 *
 * @author Yohann.
 */
public class ConsumerProcessor implements Processor {

    public static final ConsumerProcessor PROCESSOR = new ConsumerProcessor();

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
