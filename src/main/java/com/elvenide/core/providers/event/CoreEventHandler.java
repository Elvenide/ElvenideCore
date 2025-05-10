package com.elvenide.core.providers.event;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotates {@link CoreEvent} handler methods within a {@link CoreListener}.
 * <p>
 * Similar to Bukkit's {@link org.bukkit.event.EventHandler @EventHandler}.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
@Inherited
public @interface CoreEventHandler {

    /**
     * The priority of the event.
     * <p>
     * The priorities are executed from first to last in the following order:
     * <ol>
     *     <li>EARLIEST
     *     <li>EARLY
     *     <li>NORMAL (default)
     *     <li>LATE
     *     <li>LATEST
     *     <li>RESULT
     * </ol>
     * @return The priority
     */
    CoreEventPriority priority() default CoreEventPriority.NORMAL;

    /**
     * If true, this handler method will not run if the event was cancelled by an earlier executed handler.
     * @return Boolean to ignore cancelled events
     */
    boolean ignoreCancelled() default false;

}
