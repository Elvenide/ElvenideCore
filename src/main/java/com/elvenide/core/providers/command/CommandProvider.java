package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.plugin.PluginProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#commands} field.
 */
public class CommandProvider extends Provider {

    @ApiStatus.Internal
    public CommandProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Creates a command with the provided name.
     * @param name Name of the command
     * @return Builder function to configure the command
     */
    @Contract(pure = true)
    @PublicAPI
    public CommandBuilder create(String name) {
        return CommandRegistry.command(core, name);
    }

    /**
     * Registers all commands created by {@link #create(String)}.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
     * @see #create(String)
     * @since 0.0.15
     */
    @PublicAPI
    public void register() {
        CommandRegistry.register(core, Core.plugin.get());
    }

    /**
     * Sets the command header, displayed above command usage information.
     * @param header The command header
     * @see Core.lang#COMMAND_HEADER
     * @since 0.0.9
     */
    @PublicAPI
    public void setHeader(String header) {
        Core.lang.COMMAND_HEADER.set(header);
    }

}
