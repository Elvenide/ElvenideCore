package com.elvenide.core.providers;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.plugin.CorePlugin;
import com.elvenide.core.providers.commands.CommandBuilder;
import com.elvenide.core.providers.commands.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

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
    public void register(JavaPlugin plugin) {
        CommandRegistry.register(core, plugin);
    }

    /**
     * Registers all commands created by {@link #create(String)}.
     * <p>
     * <b>If you ARE NOT using {@link CorePlugin}</b>: you must do {@link Core#setPlugin(JavaPlugin) manual initialization}
     * before using the register method.
     * <p>
     * <b>If you ARE using {@link CorePlugin}</b>: you do not need to use the register method, as commands are registered automatically.
     * @see #create(String)
     * @since 0.0.15
     */
    public void register() {
        CommandRegistry.register(core, core.plugin);
    }

    /**
     * Sets the command header, displayed above command usage information.
     * @param header The command header
     * @see LangProvider.CommonLangKeys#COMMAND_HEADER
     * @since 0.0.9
     */
    public void setHeader(String header) {
        Core.lang.common.setCommandHeader(header);
    }

}
