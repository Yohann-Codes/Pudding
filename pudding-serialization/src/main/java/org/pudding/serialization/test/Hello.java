package org.pudding.serialization.test;

import java.io.Serializable;

/**
 * @author Yohann.
 */
public class Hello implements Serializable {
    private String content;

    public Hello() {
    }

    public Hello(String content) {
        this.content = content;
    }

    public void sayHello() {
        System.out.println(content);
    }
}
