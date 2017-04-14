package org.pudding.registry;

/**
 * The interface of default registry_cluster.
 *
 * @author Yohann.
 */
public interface PuddingServiceRegistry extends ServiceRegistry {

    /**
     * Join up registry_cluster cluster, which is connect with the last registry server.
     * <p>
     * Notice:
     * You must call {@link org.pudding.registry.config.RegistryConfig#setClusterAddress(String...) to configure
     * the registry_cluster cluster address before invoke this method. Otherwise, throw {@link IllegalStateException}.
     */
    void joinUpCluster();

    /**
     * Join up registry_cluster cluster, which is connect with the last registry server.
     *
     * @param prevAddress all registry's address
     */
    void joinUpCluster(String... prevAddress);

    /**
     * Drop out cluster, which is disconnect with other registry servers.
     */
    void dropOutCluster();

    /**
     * Return the number of worker thread.
     */
    int workers();

    /**
     * Shutdown the {@link PuddingServiceRegistry}.
     * <p>
     * Notice:
     * If you call the method to shudown the current {@link PuddingServiceRegistry}, you must new
     * a {@link PuddingServiceRegistry}'s instance before operate the instance. Otherwise, throw {@link IllegalStateException}.
     */
    void shutdown();
}
