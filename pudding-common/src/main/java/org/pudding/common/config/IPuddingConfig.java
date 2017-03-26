package org.pudding.common.config;

/**
 * Pudding配置接口.
 *
 * @author Yohann.
 */
public interface IPuddingConfig {

    /**
     * 序列化方式
     *
     * @param type
     */
    void serializerType(byte type);
}
