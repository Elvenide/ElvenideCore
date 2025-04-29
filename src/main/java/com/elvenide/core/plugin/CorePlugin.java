package com.elvenide.core.plugin;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.events.CoreReloadEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * ElvenideCore's powerful extension of the {@link JavaPlugin} class.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public abstract class CorePlugin extends JavaPlugin {

    private static CorePlugin instance;

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
     * This method is executed before the first {@link #onReload() reload}
     * (Load >> Enable >> Reload).
     * @since 0.0.15
     */
    @ApiStatus.OverrideOnly
    public abstract void onEnabled();

    /**
     * Code run whenever the plugin is reloaded (including initial load).
     * This method is first executed after the plugin is {@link #onEnabled() enabled}
     * (Load >> Enable >> Reload).
     * @since 0.0.15
     */
    @ApiStatus.OverrideOnly
    public abstract void onReload();

    /**
     * Code run when the plugin is disabled.
     * @since 0.0.15
     */
    @ApiStatus.OverrideOnly
    public void onDisabled() {}

    /**
     * Registers one or more event listeners.
     * @param listeners Listeners
     * @since 0.0.15
     */
    public static void registerListeners(Listener ...listeners) {
        for (Listener listener : listeners)
            instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    /**
     * Manually reloads the plugin (e.g. to reload config files).
     * Automatically calls {@link #onReload()} after finishing the reload.
     * @since 0.0.15
     */
    public static void reload() {
        new CoreReloadEvent(instance).callEvent();
        instance.onReload();
    }

}
