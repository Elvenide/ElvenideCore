package com.elvenide.core.providers.command;

import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a group of several {@link SubCommand} defined by various member methods.
 * This allows logically grouping multiple related subcommands.
 * <p>
 * To add a new subcommand to this group, do both of the following:
 * <ul>
 *     <li>Create a method annotated by {@link SubCommandSetup @SubCommandSetup(subCmdName)} with parameter of type {@link SubCommandBuilder}</li>
 *     <li>Create a method annotated by {@link SubCommandHandler @SubCommandHandler(subCmdName)} with parameter of type {@link SubCommandContext}</li>
 * </ul>
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 * @since 25.1
 */
@PublicAPI
public interface SubGroup extends SubNode {

    /**
     * The name of the subgroup.
     * @return String name
     */
    @NotNull String label();

}
