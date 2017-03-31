package org.pudding.example.service;

/**
 * @author Yohann.
 */
public class MyServiceImplA implements MyServiceA {
    @Override
    public void method1() {
        System.out.println("MyServiceImplA.method1()");
    }

    @Override
    public void method2(String param) {
        System.out.println("MyServiceImplA.method2(param) param: " + param);
    }

    @Override
    public String method3() {
        System.out.println("MyServiceImplA.method3()");
        return "return: MyServiceImplA.method3()";
    }

    @Override
    public String method4(String param) {
        System.out.println("MyServiceImplA.method4(param) param: " + param);
        return "return: MyServiceImplA.method4(param) param: " + param;
    }
}
