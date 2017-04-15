package org.pudding.transport.protocol;

/**
 * Hold message.
 *
 * @author Yohann.
 */
public final class Message {
    private ProtocolHeader header;
    private byte[] body;

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
