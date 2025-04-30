package com.elvenide.core.plugin;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.events.CoreReloadEvent;
import com.elvenide.core.providers.config.ConfigSupplier;
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
public abstract class CorePlugin extends JavaPlugin {

    private static CorePlugin instance = null;
    private final HashSet<ConfigSupplier> configSuppliers = new HashSet<>();

    /// Only for internal use. Use {@link #onLoaded()} instead.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onLoad() {
        instance = this;
        ElvenideCore.setPlugin(this);
        this.onLoaded();
    }

    /// Only for internal use. Use {@link #onEnabled()} instead.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onEnable() {
        this.onEnabled();
        reload();
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
    @ApiStatus.OverrideOnly
    public void onDisabled() {}

    /**
     * Registers one or more config suppliers.
     * Registered config suppliers will be automatically reloaded when {@link #reload()} is called.
     * @param suppliers Config suppliers
     * @since 0.0.15
     */
    public final void registerConfigSuppliers(ConfigSupplier... suppliers) {
        configSuppliers.addAll(List.of(suppliers));
    }

    /**
     * Registers one or more event listeners.
     * @param listeners Listeners
     * @since 0.0.15
     */
    public static void registerListeners(Listener... listeners) {
        if (instance == null)
            throw new IllegalStateException("Plugin has not been initialized yet.");

        for (Listener listener : listeners)
            instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    /**
     * Manually reloads the plugin (e.g. to reload config files).
     * Emits a {@link CoreReloadEvent} and reloads all registered {@link #registerConfigSuppliers(ConfigSupplier...) config suppliers}.
     * @since 0.0.15
     */
    public static void reload() {
        if (instance == null)
            throw new IllegalStateException("Plugin has not been initialized yet.");

        // Reload config suppliers
        for (ConfigSupplier supplier : instance.configSuppliers)
            supplier.reload();

        // Emit reload event
        new CoreReloadEvent(instance).callEvent();
    }

}
