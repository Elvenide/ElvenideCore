package com.elvenide.core.events;

import org.jetbrains.annotations.Contract;

/**
 * Represents the result of a {@link CoreEvent} call.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public enum CoreEventResult {
    /// The event was cancelled.
    CANCELLED,

    /// The event was completed without cancellation.
    COMPLETED;

    /**
     * Checks if the event was cancelled.
     * @return {@code true} if the event was cancelled, {@code false} otherwise
     * @since 0.0.15
     */
    @Contract(pure = true)
    public boolean isCancelled() {
        return this == CANCELLED;
    }
}
