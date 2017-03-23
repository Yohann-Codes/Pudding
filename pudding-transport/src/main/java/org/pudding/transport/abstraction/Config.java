package org.pudding.transport.abstraction;

import org.pudding.transport.options.Option;

/**
 * 参数配置接口.
 *
 * @author Yohann.
 */
public interface Config {

    /**
     * 设置Pudding选项.
     *
     * @param option
     * @param <T>
     */
    <T> Config option(Option<T> option, T value);
}
