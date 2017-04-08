package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;

/**
 * The encoder.
 *
 * @author Yohann.
 */
@ChannelHandler.Sharable
public class ProtocolEncoder extends MessageToByteEncoder<MessageHolder> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageHolder msg, ByteBuf out) throws Exception {
        validate(msg);

        ProtocolHeader header = msg.getHeader();
        byte[] body = msg.getBody();

        out.writeShort(header.getMagic())
                .writeByte(header.getType())
                .writeByte(header.getSign())
                .writeLong(header.getInvokeId())
                .writeInt(header.getResultCode())
                .writeInt(header.getBodyLength())
                .writeBytes(body);
    }

    private void validate(MessageHolder msg) {
        if (msg == null) {
            throw new NullPointerException("msg == null");
        }
        if (msg.getHeader() == null || msg.getBody() == null) {
            throw new NullPointerException("msg.getHeader() == null || msg.getBody() == null");
        }
    }
}
