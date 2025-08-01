package com.elvenide.core.providers.event;

import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a listener that can handle CoreEvent events,
 * including both built-in ElvenideCore events and your own custom events.
 * <p></p>
 * Individual methods in the class implementing this interface should be annotated
 * with {@link CoreEventHandler @CoreEventHandler} to allow them to receive events, similar to Bukkit's {@link org.bukkit.event.Listener}.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@PublicAPI
public interface CoreListener {

    /**
     * Registers this core listener, allowing it to receive core events.
     * @since 0.0.15
     */
    @PublicAPI
    default void register() {
        CoreEventManager.register(this);
    }

    /**
     * Unregisters this core listener, stopping it from receiving core events.
     * @since 0.0.15
     */
    @PublicAPI
    default void unregister() {
        CoreEventManager.unregister(this);
    }

    /**
     * Unregisters all core listeners of the given class.<br/>
     * If you just want to unregister a single core listener, use the non-static {@link #unregister()} instead.
     * @param listenerClass The class of the listeners to unregister
     * @since 0.0.15
     */
    @PublicAPI
    static void unregisterListeners(Class<? extends CoreListener> listenerClass) {
        CoreEventManager.unregisterAllWithListener(listenerClass);
    }

    /**
     * Unregisters all core listeners.
     * <p></p>
     * It is not recommended to use this, as it could affect internal ElvenideCore functionality or dependent
     * libraries' functionality in the future.
     * @since 0.0.15
     */
    @PublicAPI
    @ApiStatus.Experimental
    static void unregisterAll() {
        CoreEventManager.unregisterAll();
    }

}
