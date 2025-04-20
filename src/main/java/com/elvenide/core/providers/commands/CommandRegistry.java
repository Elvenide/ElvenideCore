package com.elvenide.core.providers.commands;

import com.elvenide.core.ElvenideCore;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.List;

/**
 * Used internally by ElvenideCore to manage and register commands.
 * To register your own commands, use <code>ElvenideCore.commands.register()</code>
 * @see ElvenideCore#commands
 */
@ApiStatus.Internal
public final class CommandRegistry {
    private CommandRegistry() {}

    static final HashSet<CommandBuilder> commands = new HashSet<>();

    public static CommandBuilder command(ElvenideCore core, String name) {
        if (core == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        return new CommandBuilder(name);
    }

    public static void register(ElvenideCore core, JavaPlugin plugin) {
        if (core == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, cmdRegistry -> {
            for (CommandBuilder builder : commands)
                cmdRegistry.registrar().register(builder.build(), builder.description, List.of(builder.aliases));
        });
    }

}
