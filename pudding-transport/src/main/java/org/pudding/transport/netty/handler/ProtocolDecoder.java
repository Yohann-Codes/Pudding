package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.pudding.transport.common.MessageHolder;
import org.pudding.transport.common.ProtocolHeader;

import java.util.List;

/**
 * 解码Handler.
 *
 * @author Yohann.
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < ProtocolHeader.HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();

        if (in.readShort() != ProtocolHeader.MAGIC) {
            in.resetReaderIndex();
            return;
        }

        byte type = in.readByte();
        byte sign = in.readByte();
        long id = in.readLong();
        int errorCode = in.readInt();
        int bodyLength = in.readInt();

        if (in.readableBytes() != bodyLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[bodyLength];
        in.readBytes(body);

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(type);
        header.setSign(sign);
        header.setId(id);
        header.setErrorCode(errorCode);
        header.setBodyLength(bodyLength);

        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);

        out.add(holder);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
