package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.lang.LangProvider;
import com.elvenide.core.providers.plugin.CorePlugin;
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
     * @param plugin Your plugin
     * @see #create(String)
     * @deprecated Use {@link #register()} with initialization instead.
     */
    @Deprecated(since = "0.0.15", forRemoval = true)
    @PublicAPI
    public void register(JavaPlugin plugin) {
        CommandRegistry.register(core, plugin);
    }

    /**
     * Registers all commands created by {@link #create(String)}.
     * <p></p>
     * <b>If you ARE NOT using {@link CorePlugin}</b>: you must do {@link Core#setPlugin(JavaPlugin) manual initialization}
     * before using the register method.
     * <p></p>
     * <b>If you ARE using {@link CorePlugin}</b>: you do not need to use the register method, as commands are registered automatically.
     * @see #create(String)
     * @since 0.0.15
     */
    @PublicAPI
    public void register() {
        CommandRegistry.register(core, core.plugin);
    }

    /**
     * Sets the command header, displayed above command usage information.
     * @param header The command header
     * @see LangProvider.CommonLangKeys#COMMAND_HEADER
     * @since 0.0.9
     */
    @PublicAPI
    public void setHeader(String header) {
        Core.lang.common.setCommandHeader(header);
    }

}
