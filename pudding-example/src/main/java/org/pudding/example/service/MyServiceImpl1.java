package org.pudding.example.service;

/**
 * @author Yohann.
 */
public class MyServiceImpl1 implements MyService1 {
    @Override
    public void sayHello(String param) {
        System.out.println("hello " + param);
    }
}
