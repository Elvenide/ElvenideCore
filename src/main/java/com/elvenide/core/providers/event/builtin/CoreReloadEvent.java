package com.elvenide.core.providers.event.builtin;

import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.config.ConfigProvider;
import com.elvenide.core.providers.event.CoreEvent;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event automatically fired when {@link ConfigProvider#reloadSuppliers() Core.config.reloadSuppliers()} is called.
 * <p>
 * Supports both {@link org.bukkit.event.Listener Bukkit} and {@link com.elvenide.core.providers.event.CoreListener ElvenideCore} listeners.
 */
@PublicAPI
public class CoreReloadEvent extends Event implements CoreEvent {

    private static final HandlerList handlers = new HandlerList();

    @PublicAPI
    public CoreReloadEvent() {}

    @PublicAPI
    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @PublicAPI
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
