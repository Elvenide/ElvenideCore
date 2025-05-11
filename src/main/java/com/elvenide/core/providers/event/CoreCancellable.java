package com.elvenide.core.providers.event;

import com.elvenide.core.api.PublicAPI;

/**
 * Represents a {@link CoreEvent} that can be cancelled.
 * @since 0.0.15
 * @author <a href="https://elvenide.com">Elvenide</a>
 */
@PublicAPI
public interface CoreCancellable {

    /**
     * Sets the event as cancelled, preventing later-priority handlers marked "ignoreCancelled" from running.
     * @param cancelled Whether the event is cancelled
     */
    @PublicAPI
    default void setCancelled(boolean cancelled) {
        throw new CoreEventCancelException(cancelled);
    }

}
