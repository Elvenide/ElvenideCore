package com.elvenide.core.events;

import com.elvenide.core.Core;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.ApiStatus;

/**
 * For internal use only.
 * <p>
 * For use in your plugin, use {@link Core.Event} instead.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@ApiStatus.Internal
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
