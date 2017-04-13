package org.pudding.example.service;

import java.io.Serializable;

/**
 * @author Yohann.
 */
public interface MyServiceA extends Serializable {
    int add(int a, int b);

    int subtract(int a, int b);
}
