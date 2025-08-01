package com.elvenide.core.providers.config;

import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.plugin.CorePlugin;

import java.util.List;

/**
 * When registered in a CorePlugin, all configs supplied by this ConfigSupplier will automatically
 * be reloaded when the plugin is reloaded by {@link CorePlugin#reload()}.
 * <p></p>
 * Can be registered within a CorePlugin instance using {@link CorePlugin#registerConfigSuppliers(ConfigSupplier...)}.
 * @since 0.0.15
 */
@PublicAPI
public interface ConfigSupplier {

    /**
     * Gets all reloadable configs supplied by this ConfigSupplier.
     * @return List of Config
     * @since 0.0.15
     */
    List<Config> configs();

    /**
     * Manually reloads all configs supplied by this ConfigSupplier.
     * Should not be used in most cases, as {@link CorePlugin#reload()} calls this method automatically.
     * @since 0.0.15
     */
    default void reload() {
        for (Config config : configs())
            config.reload();
    }

}
