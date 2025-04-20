package com.elvenide.core.providers.commands;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.function.Consumer;

public class SubGroupBuilder {

    private final String label;
    private final LinkedList<SubNode> subNodes = new LinkedList<>();

    SubGroupBuilder(String label) {
        this.label = label;
    }

    public SubGroupBuilder addSubCommand(SubCommand subCommand) {
        subNodes.add(subCommand);
        return this;
    }

    public SubGroupBuilder addSubGroup(String name, Consumer<SubGroupBuilder> consumer) {
        SubGroupBuilder subGroupBuilder = new SubGroupBuilder(name);
        consumer.accept(subGroupBuilder);
        subNodes.add(subGroupBuilder.build());
        return this;
    }

    SubGroup build() {
        return new SubGroup(subNodes) {
            @Override
            public @NotNull String label() {
                return label;
            }
        };
    }

}
