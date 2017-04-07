package org.pudding.transport.api;

import java.util.List;

/**
 * Pudding transport option configuration.
 *
 * @author Yohann.
 */
public interface OptionConfig {

    /**
     * Configure {@link Option}.
     */
    <T> boolean setOption(Option<T> option, T value);

    /**
     * Return all {@link Option}'s.
     */
    List<Option<?>> getOptions();

    /**
     * Return the value of the given {@link Option}.
     */
    <T> T getOption(Option<T> option);
}
