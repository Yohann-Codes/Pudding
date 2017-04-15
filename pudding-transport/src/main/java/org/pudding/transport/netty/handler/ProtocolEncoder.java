package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

/**
 * The encoder.
 *
 * @author Yohann.
 */
@ChannelHandler.Sharable
public class ProtocolEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        validate(msg);

        ProtocolHeader header = msg.getHeader();
        byte[] body = msg.getBody();

        out.writeShort(header.getMagic())
                .writeByte(header.getType())
                .writeByte(header.getSign())
                .writeLong(header.getSequence())
                .writeInt(header.getStatus())
                .writeInt(header.getBodyLength())
                .writeBytes(body);
    }

    private void validate(Message msg) {
        if (msg == null) {
            throw new NullPointerException("msg == null");
        }
        if (msg.getHeader() == null || msg.getBody() == null) {
            throw new NullPointerException("msg.getHeader() == null || msg.getBody() == null");
        }
    }
}
