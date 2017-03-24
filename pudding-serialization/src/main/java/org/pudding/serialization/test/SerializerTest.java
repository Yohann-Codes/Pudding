package org.pudding.serialization.test;

import org.pudding.serialization.api.Serializer;
import org.pudding.serialization.api.SerializerFactory;
import org.pudding.serialization.api.SerializerType;

/**
 * @author Yohann.
 */
public class SerializerTest {
    private Serializer serializer;

    public static void main(String[] args) {
        SerializerTest test = new SerializerTest();
        test.javaTest();
        test.kryoTest();
        test.hessianTest();
        test.gsonTest();
    }

    private void kryoTest() {
        Hello hello = new Hello("hello kryo");
        serializer = SerializerFactory.getSerializer(SerializerType.KRYO.value());
        long nanoTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            byte[] bytes = serializer.writeObject(hello);
        }
//        Hello rHello = serializer.readObject(bytes, Hello.class);
        System.out.println("   Kryo time = " + (System.nanoTime() - nanoTime));
//        rHello.sayHello();
    }

    public void hessianTest() {
        Hello hello = new Hello("hello hessian");
        serializer = SerializerFactory.getSerializer(SerializerType.HESSIAN.value());
        long nanoTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            byte[] bytes = serializer.writeObject(hello);
        }
//        Hello rHello = serializer.readObject(bytes, Hello.class);
        System.out.println("Hessian time = " + (System.nanoTime() - nanoTime));
//        rHello.sayHello();
    }

    public void javaTest() {
        Hello hello = new Hello("hello java");
        serializer = SerializerFactory.getSerializer(SerializerType.JAVA.value());
        long nanoTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            byte[] bytes = serializer.writeObject(hello);
        }
//        Hello rHello = serializer.readObject(bytes, Hello.class);
        System.out.println("   Java time = " + (System.nanoTime() - nanoTime));
//        rHello.sayHello();
    }

    public void gsonTest() {
        Hello hello = new Hello("hello gson");
        serializer = SerializerFactory.getSerializer(SerializerType.GSON.value());
        long nanoTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            byte[] bytes = serializer.writeObject(hello);
        }
//        Hello rHello = serializer.readObject(bytes, Hello.class);
        System.out.println("   Gson time = " + (System.nanoTime() - nanoTime));
//        rHello.sayHello();
    }
}
