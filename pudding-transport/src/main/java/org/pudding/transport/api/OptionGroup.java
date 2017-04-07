package org.pudding.transport.api;

import org.pudding.transport.netty.ChildOption;
import org.pudding.transport.netty.ParentOption;

/**
 * Option Group.
 *
 * @author Yohann.
 */
public interface OptionGroup {

    /**
     * Returns the {@link ParentOption}'s instance.
     */
    OptionConfig parentOption();

    /**
     * Returns the {@link ChildOption}'s instance.
     */
    OptionConfig childOption();
}
