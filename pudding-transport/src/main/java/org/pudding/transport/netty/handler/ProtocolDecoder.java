package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

import java.util.List;

/**
 * The decoder.
 *
 * @author Yohann.
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    private static final Logger logger = Logger.getLogger(ProtocolDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < ProtocolHeader.HEAD_LENGTH) {
            logger.info("readable bytes less than protocol header "
                    + in.readableBytes() + " < " + ProtocolHeader.HEAD_LENGTH);
            return;
        }
        in.markReaderIndex();

        // Check magic
        checkMagic(in);

        byte type = in.readByte();
        byte sign = in.readByte();
        long sequence = in.readLong();
        int status = in.readInt();
        int bodyLength = in.readInt();

        // Check bodylength
        checkBodyLength(in, bodyLength);

        byte[] body = new byte[bodyLength];
        in.readBytes(body);

        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(ProtocolHeader.MAGIC)
                .setType(type)
                .setSign(sign)
                .setSequence(sequence)
                .setStatus(status)
                .setBodyLength(bodyLength);

        Message holder = new Message();
        holder.setHeader(header)
                .setBody(body);

        out.add(holder);
    }

    private void checkMagic(ByteBuf in) {
        if (in.readShort() != ProtocolHeader.MAGIC) {
            logger.info("wrong magic " + in.readShort() + " != " + ProtocolHeader.MAGIC);
            in.resetReaderIndex();
            return;
        }
    }

    private void checkBodyLength(ByteBuf in, int bodyLength) {
        if (in.readableBytes() < bodyLength) {
            logger.info("readable bytes less than body " + in.readableBytes() + " < " + bodyLength);
            in.resetReaderIndex();
            return;
        }
    }
}
