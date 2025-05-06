package com.elvenide.core.events;

/**
 * Represents a {@link CoreEvent} that can be cancelled.
 * @since 0.0.15
 * @author <a href="https://elvenide.com">Elvenide</a>
 */
public interface CoreCancellable {

    /**
     * Sets the event as cancelled, preventing later-priority handlers marked "ignoreCancelled" from running.
     * @param cancelled Whether the event is cancelled
     */
    default void setCancelled(boolean cancelled) {
        throw new CoreEventCancelException(cancelled);
    }

}
