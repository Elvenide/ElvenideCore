package com.elvenide.core.providers.command;

import com.elvenide.core.api.PublicAPI;
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

    /**
     * Adds a new subcommand as a child to this subgroup.
     * @param subCommand The subcommand
     * @return This
     */
    @PublicAPI
    public SubGroupBuilder addSubCommand(SubCommand subCommand) {
        group.addSubNode(new NodeWrapper(subCommand, hashedGroup));
        return this;
    }

    /**
     * Adds a new subgroup as a child to this subgroup.
     * @param name The name of the new subgroup
     * @param consumer The consumer to build the new subgroup
     * @return This
     */
    @PublicAPI
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
