package org.pudding.transport.protocol;

import org.pudding.common.utils.SequenceUtil;

/**
 * Network message.
 *
 * @author Yohann.
 */
public final class Message {
    private final long sequence;

    private ProtocolHeader header;
    private byte[] body;

    public Message() {
        sequence = SequenceUtil.generateSequence();
    }

    public long getSequence() {
        return sequence;
    }

    public ProtocolHeader getHeader() {
        return header;
    }

    public Message setHeader(ProtocolHeader header) {
        this.header = header;
        return this;
    }

    public byte[] getBody() {
        return body;
    }

    public Message setBody(byte[] body) {
        this.body = body;
        return this;
    }
}
