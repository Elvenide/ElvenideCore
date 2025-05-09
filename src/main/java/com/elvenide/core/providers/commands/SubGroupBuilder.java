package com.elvenide.core.providers.commands;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SubGroupBuilder {

    private final SubGroup group;
    private final NodeWrapper hashedGroup;

    SubGroupBuilder(String label, NodeWrapper parent) {
        this.group = new SubGroup() {
            @Override
            public @NotNull String label() {
                return label;
            }
        };
        this.hashedGroup = new NodeWrapper(group, parent);
    }

    public SubGroupBuilder addSubCommand(SubCommand subCommand) {
        group.addSubNode(new NodeWrapper(subCommand, hashedGroup));
        return this;
    }

    public SubGroupBuilder addSubGroup(String name, Consumer<SubGroupBuilder> consumer) {
        SubGroupBuilder subGroupBuilder = new SubGroupBuilder(name, hashedGroup);
        consumer.accept(subGroupBuilder);
        group.addSubNode(subGroupBuilder.buildHashed());
        return this;
    }

    NodeWrapper buildHashed() {
        return hashedGroup;
    }

}
