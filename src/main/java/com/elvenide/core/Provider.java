package com.elvenide.core;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Provider {

    protected final @NotNull ElvenideCore core;

    @ApiStatus.Internal
    public Provider(@Nullable ElvenideCore core) {
        if (core == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        this.core = core;
    }

    protected void ensureInitialized() throws IllegalStateException {
        if (core.plugin == null)
            throw new IllegalStateException("Your plugin is using ElvenideCore features that require initialization, please do so via ElvenideCore.setPlugin()");
    }

}
