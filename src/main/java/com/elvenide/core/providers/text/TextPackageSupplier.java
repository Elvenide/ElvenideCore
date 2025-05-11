package com.elvenide.core.providers.text;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;

/**
 * When installed, supplies a bundle of custom ElvenideCore tags to {@link Core#text}.
 */
@PublicAPI
public interface TextPackageSupplier {

    /**
     * Used internally to create the package.
     * To install an existing package, use {@link #install()} instead.
     * @param textProvider The text provider
     * @see #install()
     */
    void build(TextProvider textProvider);

    /**
     * Installs the current package to {@link Core#text}.
     */
    @PublicAPI
    default void install() {
        build(Core.text);
    }

}
