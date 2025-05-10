package com.elvenide.core.providers.event;

import org.jetbrains.annotations.Nullable;

/// A pseudo-exception used internally to transmit cancellation information to the event manager.
class CoreEventCancelException extends RuntimeException {

    /// Provide 'true' to cancel the event, 'false' to un-cancel, or 'null' to keep the existing state.
    public CoreEventCancelException(@Nullable Boolean cancel) {
        super(cancel == null ? "" : cancel.toString());
    }

    public boolean isCancelled(boolean existingState) {
        if (getMessage().isBlank())
            return existingState;
        return Boolean.parseBoolean(getMessage());
    }
}
