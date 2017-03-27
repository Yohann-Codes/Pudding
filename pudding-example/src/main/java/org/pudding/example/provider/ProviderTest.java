package org.pudding.example;

import org.pudding.rpc.provider.DefaultServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderTest {
    public static void main(String[] args) {
        DefaultServiceProvider provider = new DefaultServiceProvider();
        provider.publishService(null);
    }
}
