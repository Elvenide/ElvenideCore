package com.elvenide.core.providers.config;

import com.elvenide.core.api.PublicAPI;

/**
 * Represents a config-like object that can be reloaded by a {@link ConfigSupplier}.
 * @since 0.0.19
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 */
@PublicAPI
public interface Reloadable {

    /**
     * Reloads the reloadable config-like object.
     */
    @PublicAPI
    void reload();

}
