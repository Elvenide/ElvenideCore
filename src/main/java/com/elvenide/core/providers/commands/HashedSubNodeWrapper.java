package com.elvenide.core.providers.commands;

import java.util.UUID;

class HashedSubNodeWrapper {

    private static final String SUBGROUP_PREFIX = "subgroup:";
    private static final String SUBCOMMAND_PREFIX = "subcommand:";
    private final SubNode subNode;
    private final String id;

    HashedSubNodeWrapper(SubNode subNode) {
        this.subNode = subNode;
        this.id = (isSubGroup() ? SUBGROUP_PREFIX : (isSubCommand() ? SUBCOMMAND_PREFIX : ":")) + subNode.label() + "-" + UUID.randomUUID();
    }

    public String id() {
        return id;
    }

    public SubNode asSubNode() {
        return subNode;
    }

    public SubCommand asSubCommand() {
        return (SubCommand) subNode;
    }

    public SubGroup asSubGroup() {
        return (SubGroup) subNode;
    }

    public boolean isSubCommand() {
        return subNode instanceof SubCommand;
    }

    public boolean isSubGroup() {
        return subNode instanceof SubGroup;
    }

    public static boolean isSubCommandId(String id) {
        return id.startsWith(SUBCOMMAND_PREFIX);
    }

}
