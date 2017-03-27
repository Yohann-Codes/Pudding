package org.pudding.example.provider;

import org.pudding.rpc.provider.DefaultServiceProvider;

/**
 * @author Yohann.
 */
public class ProviderTest {
    public static void main(String[] args) {
        DefaultServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20000");
    }
}
