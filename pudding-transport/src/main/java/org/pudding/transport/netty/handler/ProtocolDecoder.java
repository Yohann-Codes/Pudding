package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;
import org.pudding.common.protocol.MessageHolder;
import org.pudding.common.protocol.ProtocolHeader;

import java.util.List;

/**
 * 解码Handler.
 *
 * @author Yohann.
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    private static final Logger logger = Logger.getLogger(ProtocolDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < ProtocolHeader.HEAD_LENGTH) {
            logger.info("数据包长度小于协议头长度 " + in.readableBytes() + " < " + ProtocolHeader.HEAD_LENGTH);
            return;
        }
        in.markReaderIndex();

        if (in.readShort() != ProtocolHeader.MAGIC) {
            logger.info("Magic不一致 " + in.readShort() + " != " + ProtocolHeader.MAGIC);
            in.resetReaderIndex();
            return;
        }

        byte type = in.readByte();
        byte sign = in.readByte();
        long invokeId = in.readLong();
        int errorCode = in.readInt();
        int bodyLength = in.readInt();

        if (in.readableBytes() != bodyLength) {
            logger.info("协议体长度不一致 " + in.readableBytes() + " != " + bodyLength);
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[bodyLength];
        in.readBytes(body);

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC);
        header.setType(type);
        header.setSign(sign);
        header.setInvokeId(invokeId);
        header.setErrorCode(errorCode);
        header.setBodyLength(bodyLength);

        MessageHolder holder = new MessageHolder();
        holder.setHeader(header);
        holder.setBody(body);

        out.add(holder);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
