package com.elvenide.core.providers.commands;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

abstract class SubGroup implements SubNode {

    private final LinkedList<HashedSubNodeWrapper> hashedSubNodeWrappers = new LinkedList<>();

    SubGroup(@NotNull List<SubNode> subNodes) {
        for (SubNode child : subNodes) {
            hashedSubNodeWrappers.add(new HashedSubNodeWrapper(child));
        }
    }

    public LinkedList<HashedSubNodeWrapper> hashedSubNodes() {
        return hashedSubNodeWrappers;
    }

}
