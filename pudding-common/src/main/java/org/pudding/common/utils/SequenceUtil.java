package org.pudding.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Sequence generator.
 *
 * @author Yohann.
 */
public class SequenceUtil {
    private static AtomicLong sequence = new AtomicLong(1);

    public static long generateSequence() {

        synchronized (SequenceUtil.class) {
            if (Long.MAX_VALUE - sequence.get() < 1) {
                // Reset id zero
                sequence.set(1);
            }
            return sequence.getAndIncrement();
        }
    }
}
