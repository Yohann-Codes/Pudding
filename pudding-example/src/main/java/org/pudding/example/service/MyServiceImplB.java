package org.pudding.example.service;

/**
 * @author Yohann.
 */
public class MyServiceImplB implements MyServiceB {
    @Override
    public void method1() {
        System.out.println("MyServiceImplB.method1()");
    }

    @Override
    public void method2(String param) {
        System.out.println("MyServiceImplB.method2(param) param: " + param);
    }

    @Override
    public String method3() {
        System.out.println("MyServiceImplB.method3()");
        return "return: MyServiceImplB.method3()";
    }

    @Override
    public String method4(String param) {
        System.out.println("MyServiceImplA.method4(param) param: " + param);
        return "return: MyServiceImplB.method4(param) param: " + param;
    }
}
