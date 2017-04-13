package org.pudding.common.utils;

import java.util.Random;

/**
 * Create random number.
 *
 * @author Yohann.
 */
public class RandomUtil {
    private static final Random RANDOM = new Random();

    /**
     * Return a int number of [0, size).
     */
    public static int getInt(int size) {
        return RANDOM.nextInt(size);
    }
}
