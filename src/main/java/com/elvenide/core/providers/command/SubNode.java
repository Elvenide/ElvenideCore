package com.elvenide.core.providers.command;

import org.jetbrains.annotations.NotNull;

/**
 * An abstract node that is part of an ElvenideCore command tree.
 * Subcommands and subgroups are subclasses of this interface.
 */
interface SubNode {

    @NotNull String label();

}
