package org.pudding.transport.api;

/**
 * Connection Manager.
 *
 * @author Yohann.
 */
public interface ConnectionManager {

//    void managerConnection();

    /**
     * Open the automatic reconnection.
     *
     * @param address
     */
    void openAutoRennection(String address);

    /**
     * Close the automatic reconnection.
     *
     * @param address
     */
    void closeAutoRennection(String address);
}
