package com.elvenide.core.providers.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class SubArgumentBuilder {

    final String label;
    final ArgumentType<?> type;
    boolean required = true;
    @Nullable Function<SubCommandContext, List<String>> suggester = null;

    SubArgumentBuilder(String label, ArgumentType<?> type) {
        this.label = label;
        this.type = type;
    }

    /**
     * Sets this argument as not required.
     * @return This
     * @since 0.0.8
     */
    public SubArgumentBuilder setOptional() {
        this.required = false;
        return this;
    }

    /**
     * Allows you to build a dynamic tab-completion list for this argument.
     * @param suggester Suggestion builder function
     * @since 0.0.8
     */
    public void suggests(@NotNull Function<SubCommandContext, List<String>> suggester) {
        this.suggester = suggester;
    }

    /**
     * Allows you to build a static tab-completion list for this argument.
     * @param staticList List of suggestions
     * @since 0.0.13
     */
    public void suggests(List<String> staticList) {
        this.suggester = context -> staticList;
    }
}
