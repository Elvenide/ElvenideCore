package com.elvenide.core.providers.event;

import com.elvenide.core.api.PublicAPI;

/**
 * Represents a custom ElvenideCore event that can be handled by registered {@link CoreListener CoreListener}s.
 * <p>
 * This interface is a powerful alternative to the Bukkit event system for custom events, as it:
 * <ul>
 *     <li>Eliminates 99% of event boilerplate code</li>
 *     <li>Allows use of <code>record</code> classes to further reduce verbosity</li>
 *     <li>Follows familiar, Bukkit-like format</li>
 * </ul>
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 * @since 0.0.15
 */
@PublicAPI
public interface CoreEvent {

    /**
     * Calls this event as a CoreEvent, to be handled by all registered {@link CoreListener}s that are listening for it.
     * @return The result of the event (cancelled or completed)
     * @since 0.0.15
     */
    @PublicAPI
    default CoreEventResult callCoreEvent() {
        boolean isCancelled = CoreEventManager.call(this);
        return isCancelled ? CoreEventResult.CANCELLED : CoreEventResult.COMPLETED;
    }

}
