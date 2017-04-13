package org.pudding.registry.config;

/**
 * The interface of default registry.
 *
 * @author Yohann.
 */
public interface PuddingServiceRegistry extends ServiceRegistry {

    /**
     * Join up registry cluster, in other word,
     *
     * @param address all registry address
     */
    void joinUpCluster(String... address);
}
