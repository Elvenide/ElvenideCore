package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an executable subcommand that can be added to a command or subgroup.
 * <p></p>
 * Implement this interface and then use {@link Core#commands} to create your own commands in ElvenideCore.
 */
@PublicAPI
public interface SubCommand extends SubNode {

    /**
     * The name of the subcommand.
     * @return String name
     */
    @Override
    @NotNull String label();

    /**
     * Configures various optional properties of the subcommand,
     * such as its description, permission, or arguments.
     * @param builder Builder to configure the subcommand
     */
    void setup(@NotNull SubCommandBuilder builder);

    /**
     * Executes the subcommand given contextual information about the execution,
     * such as the command sender or arguments.
     * @param context Context of the command execution
     */
    void executes(@NotNull SubCommandContext context);

}
