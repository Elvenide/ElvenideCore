package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an executable subcommand that can be added to a command or subgroup.
 * <p>
 * Implement this interface and then use {@link Core#commands} to create your own commands in ElvenideCore.
 */
@PublicAPI
public interface SubCommand extends SubNode {

    @Override
    @NotNull String label();

    void setup(@NotNull SubCommandBuilder builder);

    void executes(@NotNull SubCommandContext context);

}
