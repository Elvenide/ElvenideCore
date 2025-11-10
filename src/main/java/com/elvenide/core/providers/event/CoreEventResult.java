package com.elvenide.core.providers.event;

import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.Contract;

/**
 * Represents the result of a {@link CoreEvent} call.
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 * @since 0.0.15
 */
@PublicAPI
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
    @PublicAPI
    @Contract(pure = true)
    public boolean isCancelled() {
        return this == CANCELLED;
    }
}
