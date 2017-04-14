package org.pudding.common.model;

import java.io.Serializable;

/**
 * Ack.
 *
 * @author Yohann.
 */
public class Acknowledge implements Serializable {

    private final long sequence;

    public Acknowledge(long sequence) {
        this.sequence = sequence;
    }

    public long getSequence() {
        return sequence;
    }
}
