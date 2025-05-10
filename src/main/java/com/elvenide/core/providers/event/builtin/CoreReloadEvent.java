package com.elvenide.core.providers.event.builtin;

import com.elvenide.core.providers.event.CoreEvent;
import com.elvenide.core.plugin.CorePlugin;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Event automatically fired when a {@link CorePlugin} is loaded or reloaded.
 * <p>
 * Supports both {@link org.bukkit.event.Listener Bukkit} and {@link com.elvenide.core.providers.event.CoreListener ElvenideCore} listeners.
 */
public class CoreReloadEvent extends Event implements CoreEvent {

    private static final HandlerList handlers = new HandlerList();
    private final CorePlugin plugin;

    public CoreReloadEvent(CorePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the current plugin instance.
     * @return The current plugin
     */
    @Contract(pure = true)
    public CorePlugin plugin() {
        return plugin;
    }

    /**
     * Gets the current plugin instance, cast to the given class.
     * @return The current plugin
     */
    @Contract(pure = true)
    public <T extends CorePlugin> T plugin(Class<T> clazz) {
        return clazz.cast(plugin);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
