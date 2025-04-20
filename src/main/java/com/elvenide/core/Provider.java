package com.elvenide.core;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class Provider {

    @ApiStatus.Internal
    public Provider(@Nullable ElvenideCore core) {
        if (core == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");
    }

}
