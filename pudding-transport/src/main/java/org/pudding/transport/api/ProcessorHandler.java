package org.pudding.transport.api;

/**
 * 使用Processor的Handler必须实现此接口.
 *
 * @author Yohann.
 */
public interface ProcessorHandler {

    /**
     * 关联用户自定义的Processor.
     *
     * @param processor
     */
    void processor(Processor processor);
}
