package org.pudding.common.constant;

/**
 * Load balancing strategy.
 *
 * @author Yohann.
 */
public enum LoadBalanceStrategy {

    /** Random */
    RANDOM,

    /** Round */
    ROUND,

    /** Weighted random */
    WEIGHTED_RADOM,

    /** Weighted round */
    WEIGHTED_ROUND
}
