package com.elvenide.core.providers.task;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#tasks} field.
 */
public class TaskProvider extends Provider {
    @ApiStatus.Internal
    public TaskProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Returns a builder to create a new scheduled task.
     * @return Task builder
     */
    @Contract(pure = true)
    public Task builder() {
        return new Task(core);
    }
}
