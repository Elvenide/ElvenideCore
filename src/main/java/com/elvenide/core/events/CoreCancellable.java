package com.elvenide.core.events;

import com.elvenide.core.Core;
import org.jetbrains.annotations.ApiStatus;

/**
 * For internal use only.
 * <p>
 * For use in your plugin, use {@link Core.CancellableEvent} instead.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@ApiStatus.Internal
public interface CoreCancellable {

    /**
     * Sets the event as cancelled, preventing later-priority handlers marked "ignoreCancelled" from running.
     * @param cancelled Whether the event is cancelled
     */
    default void setCancelled(boolean cancelled) {
        throw new CoreEventCancelException(cancelled);
    }

}
