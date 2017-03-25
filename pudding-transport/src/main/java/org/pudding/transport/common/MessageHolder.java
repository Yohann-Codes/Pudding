package org.pudding.transport.common;

/**
 * 消息载体.
 *
 * @author Yohann.
 */
public class MessageHolder {
    private ProtocolHeader header;
    private byte[] body;

    public ProtocolHeader getHeader() {
        return header;
    }

    public void setHeader(ProtocolHeader header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
