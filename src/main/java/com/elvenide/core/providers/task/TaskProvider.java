package com.elvenide.core.providers.task;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

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
     * @deprecated All tasks must have at least one executable action.
     *             Use {@link #create(Consumer)} instead
     * @return Task builder
     */
    @PublicAPI
    @Contract(pure = true)
    @Deprecated(since = "0.0.17", forRemoval = true)
    public Task builder() {
        return new Task(core);
    }

    /**
     * Creates a new scheduled task builder with the given executable action.
     * @param consumer Consumer
     * @return Task
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public Task create(Consumer<Task> consumer) {
        return new Task(core).then(consumer);
    }
}
