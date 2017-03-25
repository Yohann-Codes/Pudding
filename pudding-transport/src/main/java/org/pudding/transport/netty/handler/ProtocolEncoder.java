package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.pudding.transport.common.MessageHolder;
import org.pudding.transport.common.ProtocolHeader;

/**
 * 编码Handler.
 *
 * @author Yohann.
 */
public class ProtocolEncoder extends MessageToByteEncoder<MessageHolder> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageHolder msg, ByteBuf out) throws Exception {
        validate(msg);

        ProtocolHeader header = msg.getHeader();
        byte[] body = msg.getBody();

        out.writeShort(header.getMagic())
                .writeByte(header.getType())
                .writeByte(header.getSign())
                .writeLong(header.getId())
                .writeInt(header.getErrorCode())
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
