package com.elvenide.core.providers.event;

import com.elvenide.core.api.PublicAPI;

/**
 * The priority of an event handler, used to determine the order in which handlers for a specific event are executed.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@PublicAPI
public enum CoreEventPriority {

    /// Handler executes before all others.
    EARLIEST,

    /// Handler executes after EARLIEST but before NORMAL handlers.
    EARLY,

    /// Handler executes after EARLY but before LATE handlers.
    NORMAL,

    /// Handler executes after NORMAL but before LATEST handlers.
    LATE,

    /// Handler executes after LATE but before RESULT handlers.
    LATEST,

    /**
     * Handler executes after all others. Should be used to view the final result without further modifying the event.
     * <p>
     * Attempting to cancel an event in a RESULT priority handler will have no effect.
     */
    RESULT

}
