package org.pudding.example;

/**
 * @author Yohann.
 */
public class MyServiceImpl implements MyService {
    @Override
    public void sayHello(String param) {
        System.out.println("hello " + param);
    }
}
