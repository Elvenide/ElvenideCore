package com.elvenide.core.providers.config;

import com.elvenide.core.api.PublicAPI;

import java.util.List;

/**
 * When registered in a CorePlugin, all configs supplied by this ConfigSupplier will automatically
 * be reloaded when the plugin is reloaded by {@link ConfigProvider#reloadSuppliers() Core.config.reloadSuppliers()}.
 * <p>
 * Can be registered within a CorePlugin instance using {@link ConfigProvider#registerSuppliers(ConfigSupplier...) Core.config.registerSuppliers()}.
 * @since 0.0.15
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 */
@PublicAPI
public interface ConfigSupplier extends Reloadable {

    /**
     * Gets all reloadable configs and config-like objects supplied by this ConfigSupplier.
     * @return List of Config
     * @since 0.0.15
     */
    @PublicAPI
    List<Reloadable> configs();

    /**
     * Manually reloads all configs and config-like objects supplied by this ConfigSupplier.
     * Should not be used in most cases, as {@link ConfigProvider#reloadSuppliers()} calls this method automatically.
     * @since 0.0.15
     */
    @PublicAPI
    @Override
    default void reload() {
        for (Reloadable configLike : configs())
            if (configLike != this)
                configLike.reload();
    }

}
