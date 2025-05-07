package com.elvenide.core.events;

import com.elvenide.core.Core;
import org.jetbrains.annotations.ApiStatus;

/**
 * For internal use only.
 * <p>
 * For use in your plugin, use {@link Core.Listener} instead.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@ApiStatus.Internal
public interface CoreListener {

    /**
     * Registers this core listener, allowing it to receive core events.
     * @since 0.0.15
     */
    default void register() {
        CoreEventManager.register(this);
    }

    /**
     * Unregisters this core listener, stopping it from receiving core events.
     * @since 0.0.15
     */
    default void unregister() {
        CoreEventManager.unregister(this);
    }

    /**
     * Unregisters all core listeners of the given class.<br/>
     * If you just want to unregister a single core listener, use the non-static {@link #unregister()} instead.
     * @param listenerClass The class of the listeners to unregister
     * @since 0.0.15
     */
    static void unregisterListeners(Class<? extends CoreListener> listenerClass) {
        CoreEventManager.unregisterAllWithListener(listenerClass);
    }

    /**
     * Unregisters all core listeners.
     * <p>
     * It is not recommended to use this, as it could affect internal ElvenideCore functionality or dependent
     * libraries' functionality in the future.
     * @since 0.0.15
     */
    @ApiStatus.Experimental
    static void unregisterAll() {
        CoreEventManager.unregisterAll();
    }

}
