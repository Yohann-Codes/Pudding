package org.pudding.example.service;

/**
 * @author Yohann.
 */
public class MyServiceImpl implements MyService {
    @Override
    public void sayHello(String param) {
        System.out.println("hello " + param);
    }
}
