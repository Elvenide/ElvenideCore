package com.elvenide.core;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core} class.
 */
@ApiStatus.Internal
public class Provider {

    protected final @NotNull Core core;

    @ApiStatus.Internal
    protected Provider(@Nullable Core core) {
        if (core == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        this.core = core;
    }

    protected void ensureInitialized() throws IllegalStateException {
        if (core.plugin == null)
            throw new IllegalStateException("Your plugin is using ElvenideCore features that require initialization, please do so via Core.setPlugin()");
    }

}
