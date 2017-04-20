package org.pudding.transport.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.log4j.Logger;
import org.pudding.transport.protocol.Message;
import org.pudding.transport.protocol.ProtocolHeader;

import java.util.List;

/**
 * The new decoder.
 *
 * @author Yohann.
 */
public class NewProtocolDecoder extends ReplayingDecoder<NewProtocolDecoder.State> {
    private static final Logger logger = Logger.getLogger(NewProtocolDecoder.class);

    private static final int MAX_BODY_LENGTH = 1024 * 1024 * 5; // 5M

    public NewProtocolDecoder() {
        super(State.MAGIC);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ProtocolHeader header = new ProtocolHeader();

        switch (state()) {
            case MAGIC:
                checkMagic(in.readShort());
                checkpoint(State.TYPE);
            case TYPE:
                header.setType(in.readByte());
                checkpoint(State.SIGN);
            case SIGN:
                header.setSign(in.readByte());
                checkpoint(State.SEQUENCE);
            case SEQUENCE:
                header.setSequence(in.readLong());
                checkpoint(State.STATUS);
            case STATUS:
                header.setStatus(in.readInt());
                checkpoint(State.BODY_LENGTH);
            case BODY_LENGTH:
                header.setBodyLength(in.readInt());
                checkpoint(State.BODY);
            case BODY:
                int bodyLength = header.getBodyLength();
                checkBodyLength(bodyLength);
                byte[] body = new byte[bodyLength];
                in.readBytes(body);

                Message message = new Message();
                message.setHeader(header)
                        .setBody(body);

                out.add(message);

                checkpoint(State.MAGIC);
        }
    }

    private void checkMagic(short magic) {
        if (magic != ProtocolHeader.MAGIC) {
            logger.warn("wrong magic " + magic + " != " + ProtocolHeader.MAGIC);
        }
    }

    private void checkBodyLength(int bodyLength) {
        if (bodyLength > MAX_BODY_LENGTH) {
            logger.warn("exceed max length of body");
        }
    }

    enum State {
        MAGIC,
        TYPE,
        SIGN,
        SEQUENCE,
        STATUS,
        BODY_LENGTH,
        BODY
    }
}
