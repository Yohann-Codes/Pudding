package org.pudding.transport.netty;

import org.pudding.transport.api.OptionConfig;
import org.pudding.transport.api.OptionGroup;

/**
 * Netty OptionGroup.
 *
 * @author Yohann.
 */
public class NettyOptionGroup implements OptionGroup {

    private final OptionConfig parentOption;
    private final OptionConfig childOption;

    public NettyOptionGroup(OptionConfig parentOption, OptionConfig childOption) {
        this.parentOption = parentOption;
        this.childOption = childOption;
    }

    @Override
    public OptionConfig parentOption() {
        return parentOption;
    }

    @Override
    public OptionConfig childOption() {
        return childOption;
    }
}
