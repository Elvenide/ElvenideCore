package com.elvenide.core.providers.text;

import com.elvenide.core.Core;
import com.elvenide.core.providers.TextProvider;

public interface TextPackageSupplier {

    /**
     * Used internally to create the package.
     * To install an existing package, use {@link #install()} instead.
     * @param textProvider The text provider
     * @see #install()
     */
    void build(TextProvider textProvider);

    /**
     * Installs the current package to <code>Core.text</code>
     */
    default void install() {
        build(Core.text);
    }

}
