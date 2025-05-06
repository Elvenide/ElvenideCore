package com.elvenide.core.events;

import com.elvenide.core.Core;
import net.kyori.adventure.audience.Audience;

/**
 * Represents a custom ElvenideCore event that can be handled by registered {@link CoreListener CoreListener}s.
 * <p>
 * This interface is a powerful alternative to the Bukkit event system for custom events, as it:
 * <ul>
 *     <li>Eliminates 99% of event boilerplate code</li>
 *     <li>Allows use of <code>record</code> classes to further reduce verbosity</li>
 *     <li>Follows familiar, Bukkit-like format</li>
 * </ul>
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public interface CoreEvent {

    /**
     * Calls this event as a CoreEvent, to be handled by all registered {@link CoreListener}s that are listening for it.
     * @return The result of the event (cancelled or completed)
     * @since 0.0.15
     */
    default CoreEventResult callCoreEvent() {
        boolean isCancelled = CoreEventManager.call(this);
        return isCancelled ? CoreEventResult.CANCELLED : CoreEventResult.COMPLETED;
    }

    /**
     * Ends the <i>current</i> event handler, preventing any code below this method call from running.
     * <p>
     * <b>Note:</b> Unlike cancelling, this does not affect other handlers listening for the same event.
     * @since 0.0.15
     */
    default void end() {
        throw new CoreEventCancelException(null);
    }

    /**
     * Ends the <i>current</i> event handler, preventing any code below this method call from running,
     * but only if the condition is true.
     * <p>
     * <b>Note:</b> Unlike cancelling, this does not affect other handlers listening for the same event.
     * @since 0.0.15
     * @param condition Condition that will end the handler if true
     */
    default void endIf(boolean condition) {
        if (condition)
            end();
    }

    /**
     * Ends the <i>current</i> event handler, preventing any code below this method call from running.
     * Sends an error message notifying a relevant audience.
     * <p>
     * <b>Note:</b> Unlike cancelling, this does not affect other handlers listening for the same event.
     * @since 0.0.15
     * @param target Target audience to send an error message to (e.g. player, group, console, server)
     * @param errorMessage Error message to send
     * @param optionalPlaceholders Optional placeholders in the error message
     */
    default void end(Audience target, String errorMessage, Object... optionalPlaceholders) {
        Core.text.send(target, "<red>" + errorMessage, optionalPlaceholders);
        end();
    }

    /**
     * Ends the <i>current</i> event handler, preventing any code below this method call from running,
     * but only if the condition is true.
     * Sends an error message notifying a relevant audience, if the condition is true.
     * <p>
     * <b>Note:</b> Unlike cancelling, this does not affect other handlers listening for the same event.
     * @since 0.0.15
     * @param condition Condition that will end the handler and send an error message if true
     * @param target Target audience to send an error message to (e.g. player, group, console, server)
     * @param errorMessage Error message to send
     * @param optionalPlaceholders Optional placeholders in the error message
     */
    default void endIf(boolean condition, Audience target, String errorMessage, Object... optionalPlaceholders) {
        if (condition)
            end(target, errorMessage, optionalPlaceholders);
    }

}
