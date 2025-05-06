package com.elvenide.core.providers;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.task.Task;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class TaskProvider extends Provider {
    @ApiStatus.Internal
    public TaskProvider(@Nullable ElvenideCore core) {
        super(core);
    }

    @Contract(pure = true)
    public Task builder() {
        return new Task(core);
    }
}
