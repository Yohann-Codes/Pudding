package org.pudding.example.service;

/**
 * @author Yohann.
 */
public class MyServiceImpl2 implements MyService2 {
    @Override
    public void sayHello(String param) {
        System.out.println("hello " + param);
    }
}
