package com.elvenide.core.providers.task;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.plugin.PluginProvider;
import com.elvenide.core.api.PublicAPI;
import org.bukkit.plugin.java.JavaPlugin;
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
     * Creates a new scheduled task builder with the given executable action.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
     * @param consumer Consumer
     * @return Task
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public Task create(Consumer<Task> consumer) {
        ensureInitialized();
        return new Task().then(consumer);
    }
}
