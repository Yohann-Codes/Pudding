package org.pudding.example.provider;

import org.pudding.rpc.provider.DefaultServiceProvider;
import org.pudding.rpc.provider.ServiceProvider;

/**
 * @author Yohann.
 */
public class Provider {
    public static void main(String[] args) {
        ServiceProvider provider = new DefaultServiceProvider();
        provider.connectRegistry("127.0.0.1:20002", "127.0.0.1:20003");
    }
}
