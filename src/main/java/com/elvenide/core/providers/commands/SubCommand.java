package com.elvenide.core.providers.commands;

import org.jetbrains.annotations.NotNull;

public interface SubCommand extends SubNode {

    @Override
    @NotNull String label();

    void setup(@NotNull SubCommandBuilder builder);

    void executes(@NotNull SubCommandContext context);

}
