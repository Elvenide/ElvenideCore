package com.elvenide.core.providers.command;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

abstract class SubGroup implements SubNode {

    private final LinkedList<NodeWrapper> nodeWrappers = new LinkedList<>();

    SubGroup() {}

    void addSubNode(@NotNull NodeWrapper subWrapper) {
        nodeWrappers.add(subWrapper);
    }

    LinkedList<NodeWrapper> getChildNodes() {
        return nodeWrappers;
    }

}
