package com.elvenide.core.providers.plugin;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.event.CoreListener;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#plugin} field.
 */
public class PluginProvider extends Provider {

    private JavaPlugin plugin;

    @ApiStatus.Internal
    public PluginProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Sets the plugin that is using ElvenideCore.
     * <p>
     * Required for some ElvenideCore features that utilize your plugin instance.
     * @param plugin Plugin
     * @since 0.0.17
     */
    @PublicAPI
    public void set(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Gets the plugin that is using ElvenideCore.
     * <p>
     * Requires initialization through {@link #set(JavaPlugin)}.
     * @return Plugin
     * @since 0.0.17
     */
    @PublicAPI
    public JavaPlugin get() {
        ensureInitialized();
        return plugin;
    }

    /**
     * Checks if ElvenideCore's plugin provider has been initialized through {@link #set(JavaPlugin)}.
     * @return True if initialized; false otherwise
     * @since 0.0.17
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @PublicAPI
    public boolean isSet() {
        return plugin != null;
    }

    /**
     * Registers one or more Bukkit event listeners.
     * <p>
     * Requires initialization through {@link #set(JavaPlugin)}.
     * <p>
     * Note: Does not register {@link CoreListener CoreListener}s; use {@link CoreListener#register()} to do so.
     * @param listeners Listeners
     * @since 0.0.17
     */
    @PublicAPI
    public void registerListeners(Listener... listeners) {
        ensureInitialized();
        for (Listener listener : listeners)
            get().getServer().getPluginManager().registerEvents(listener, get());
    }

    /**
     * Unregisters one or more Bukkit event listeners.
     * <p>
     * Requires initialization through {@link #set(JavaPlugin)}.
     * <p>
     * Note: Does not unregister {@link CoreListener CoreListener}s; use {@link CoreListener#unregister()} to do so.
     * @param listeners Listeners
     * @since 0.0.17
     */
    @PublicAPI
    public void unregisterListeners(Listener... listeners) {
        ensureInitialized();
        for (Listener listener : listeners)
            HandlerList.unregisterAll(listener);
    }
}
