package org.pudding.example.service;

/**
 * @author Yohann.
 */
public class MyServiceImpl implements MyService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int subtract(int a, int b) {
        return a - b;
    }
}
