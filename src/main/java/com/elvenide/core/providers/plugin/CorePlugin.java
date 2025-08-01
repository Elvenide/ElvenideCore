package com.elvenide.core.providers.plugin;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.config.ConfigSupplier;
import com.elvenide.core.providers.event.CoreListener;
import com.elvenide.core.providers.event.builtin.CoreReloadEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.List;

/**
 * ElvenideCore's powerful extension of the {@link JavaPlugin} class.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
@PublicAPI
public abstract class CorePlugin extends JavaPlugin {

    private static CorePlugin instance = null;
    private final HashSet<ConfigSupplier> configSuppliers = new HashSet<>();

    /// Only for internal use. Use {@link #onLoaded()} instead.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onLoad() {
        instance = this;
        Core.setPlugin(this);
        this.onLoaded();
    }

    /// Only for internal use. Use {@link #onEnabled()} instead.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onEnable() {
        this.onEnabled();
        reload();
        Core.commands.register();
    }

    /// Only for internal use. Use {@link #onDisabled()} instead.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onDisable() {
        onDisabled();
    }

    /**
     * Code run when the plugin is first loaded.
     * This method is executed before {@link #onEnabled() enabling}
     * (Load >> Enable >> Reload).
     * @since 0.0.15
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.OverrideOnly
    public void onLoaded() {}

    /**
     * Code run when the plugin is enabled.
     * This method is executed before the first {@link #reload() reload}
     * (Load >> Enable >> Reload).
     * @since 0.0.15
     */
    @ApiStatus.OverrideOnly
    public abstract void onEnabled();

    /**
     * Code run when the plugin is disabled.
     * @since 0.0.15
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.OverrideOnly
    public void onDisabled() {}

    /**
     * @return Plugin version as defined in plugin.yml or paper-plugin.yml
     * @since 0.0.15
     */
    @SuppressWarnings("UnstableApiUsage")
    @PublicAPI
    public final String getPluginVersion() {
        return getPluginMeta().getVersion();
    }

    /**
     * Registers one or more config suppliers.
     * Registered config suppliers will be automatically reloaded when {@link #reload()} is called.
     * @param suppliers Config suppliers
     * @since 0.0.15
     */
    @PublicAPI
    public final void registerConfigSuppliers(ConfigSupplier... suppliers) {
        configSuppliers.addAll(List.of(suppliers));
    }

    /**
     * Registers one or more Bukkit event listeners.
     * <p>
     * Note: Does not register {@link CoreListener CoreListener}s; use {@link CoreListener#register()} to do so.
     * @param listeners Listeners
     * @since 0.0.15
     */
    @PublicAPI
    public static void registerListeners(Listener... listeners) {
        if (instance == null)
            throw new IllegalStateException("Plugin has not been initialized yet.");

        for (Listener listener : listeners)
            instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    /**
     * Unregisters one or more Bukkit event listeners.
     * <p>
     * Note: Does not unregister {@link CoreListener CoreListener}s; use {@link CoreListener#unregister()} to do so.
     * @param listeners Listeners
     * @since 0.0.15
     */
    @PublicAPI
    public static void unregisterListeners(Listener... listeners) {
        if (instance == null)
            throw new IllegalStateException("Plugin has not been initialized yet.");

        for (Listener listener : listeners)
            HandlerList.unregisterAll(listener);
    }

    /**
     * Manually reloads the plugin (e.g. to reload config files).
     * Emits a {@link CoreReloadEvent} and reloads all registered {@link #registerConfigSuppliers(ConfigSupplier...) config suppliers}.
     * @since 0.0.15
     */
    @PublicAPI
    public static void reload() {
        if (instance == null)
            throw new IllegalStateException("Plugin has not been initialized yet.");

        // Reload config suppliers
        for (ConfigSupplier supplier : instance.configSuppliers)
            supplier.reload();

        // Emit reload event
        CoreReloadEvent event = new CoreReloadEvent(instance);
        event.callEvent(); // Emit Bukkit event
        event.callCoreEvent(); // Emit ElvenideCore event
    }

}
