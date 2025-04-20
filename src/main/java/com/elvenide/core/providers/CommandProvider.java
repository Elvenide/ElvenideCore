package com.elvenide.core.providers;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.commands.CommandBuilder;
import com.elvenide.core.providers.commands.CommandRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class CommandProvider extends Provider {
    private final ElvenideCore core;

    @ApiStatus.Internal
    public CommandProvider(@Nullable ElvenideCore core) {
        super(core);
        this.core = core;
    }

    /**
     * Creates a command with the provided name.
     * @param name Name of the command
     * @return Builder function to configure the command
     */
    public CommandBuilder create(String name) {
        return CommandRegistry.command(core, name);
    }

    /**
     * Registers all commands create with <code>create()</code>.
     * @param plugin Your plugin
     * @see #create(String)
     */
    public void register(JavaPlugin plugin) {
        CommandRegistry.register(core, plugin);
    }

    /**
     * Sets the command header, displayed above command usage information.
     * @param header The command header
     * @see LangProvider.CommonLangKeys#COMMAND_HEADER
     * @since 0.0.9
     */
    public void setHeader(String header) {
        ElvenideCore.lang.common.setCommandHeader(header);
    }

}
