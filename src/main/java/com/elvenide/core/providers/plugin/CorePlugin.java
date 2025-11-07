package com.elvenide.core.providers.plugin;

import com.elvenide.core.Core;
import com.elvenide.core.providers.config.ConfigProvider;
import com.elvenide.core.providers.config.ConfigSupplier;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * @deprecated The CorePlugin class is being phased out of ElvenideCore.
 *             Initialize your plugin using {@link PluginProvider#set(JavaPlugin) Core.plugin.set(JavaPlugin)} instead.
 */
@Deprecated(since = "0.0.17", forRemoval = true)
public abstract class CorePlugin extends JavaPlugin {

    /// Only for internal use.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onLoad() {
        Core.plugin.set(this);
        this.onLoaded();
    }

    /// Only for internal use.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onEnable() {
        this.onEnabled();
        Core.config.reloadSuppliers();
        Core.commands.register();
    }

    /// Only for internal use.
    @ApiStatus.OverrideOnly
    @ApiStatus.Internal
    @Override
    public final void onDisable() {
        onDisabled();
    }

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.OverrideOnly
    @Deprecated(since = "0.0.17", forRemoval = true)
    public void onLoaded() {}

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     */
    @ApiStatus.OverrideOnly
    @Deprecated(since = "0.0.17", forRemoval = true)
    public abstract void onEnabled();

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     */
    @SuppressWarnings("EmptyMethod")
    @ApiStatus.OverrideOnly
    @Deprecated(since = "0.0.17", forRemoval = true)
    public void onDisabled() {}

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     *             Use {@link JavaPlugin#getPluginMeta()} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public final String getPluginVersion() {
        return getPluginMeta().getVersion();
    }

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     *             Use {@link ConfigProvider#registerSuppliers(ConfigSupplier...)} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public final void registerConfigSuppliers(ConfigSupplier... suppliers) {
        Core.config.registerSuppliers(suppliers);
    }

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     *             Use {@link PluginProvider#registerListeners(Listener...)} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public static void registerListeners(Listener... listeners) {
        Core.plugin.registerListeners(listeners);
    }

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     *             Use {@link PluginProvider#unregisterListeners(Listener...)} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public static void unregisterListeners(Listener... listeners) {
        Core.plugin.unregisterListeners(listeners);
    }

    /**
     * @deprecated The CorePlugin class is being phased out of ElvenideCore.
     *             Use {@link ConfigProvider#reloadSuppliers()} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public static void reload() {
        Core.config.reloadSuppliers();
    }

}
