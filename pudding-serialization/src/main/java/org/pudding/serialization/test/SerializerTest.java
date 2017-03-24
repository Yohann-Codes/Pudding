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
//        test.javaTest();
//        test.hessianTest();
    }

    public void hessianTest() {
        Hello hello = new Hello("hello hessian");
        serializer = SerializerFactory.getSerializer(SerializerType.HESSIAN.value());
        byte[] bytes = serializer.writeObject(hello);
        Hello rHello = serializer.readObject(bytes, Hello.class);
        rHello.sayHello();
    }

    public void javaTest() {
        Hello hello = new Hello("hello java");
        serializer = SerializerFactory.getSerializer(SerializerType.JAVA.value());
        byte[] bytes = serializer.writeObject(hello);
        Hello rHello = serializer.readObject(bytes, Hello.class);
        rHello.sayHello();
    }
}
