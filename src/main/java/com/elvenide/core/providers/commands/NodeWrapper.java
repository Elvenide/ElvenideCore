package com.elvenide.core.providers.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;


public class NodeWrapper {

    private final SubNode subNode;
    private final NodeWrapper parent;
    private UsageGenerator usageGenerator = null;

    NodeWrapper(SubNode subNode, @Nullable NodeWrapper parent) {
        this.subNode = subNode;
        this.parent = parent;
    }

    /**
     * Gets the parent node of this node, if any.
     * @return The parent node, or <code>null</code> if this node is the root command
     */
    public @Nullable NodeWrapper parent() {
        return parent;
    }

    SubNode asSubNode() {
        return subNode;
    }

    SubCommand asSubCommand() {
        return (SubCommand) subNode;
    }

    SubGroup asSubGroup() {
        return (SubGroup) subNode;
    }

    boolean isSubCommand() {
        return subNode instanceof SubCommand;
    }

    boolean isSubGroup() {
        return subNode instanceof SubGroup;
    }

    /// Recursively finds the wrapper for the given subnode further down the node tree.
    @Nullable NodeWrapper findNode(SubNode node) {
        if (isSubGroup()) {
            for (NodeWrapper child : asSubGroup().getChildNodes())
                if (child.asSubNode() == node)
                    return child;
                else if (child.isSubGroup()) {
                    NodeWrapper found = child.findNode(node);
                    if (found != null)
                        return found;
                }

            return null;
        }

        return null;
    }

    /**
     * Recursively finds a subnode further down the node tree using the given path (case-insensitive).
     * <p>
     * Experimentally available to allow making your own help subcommand.
     * @param path Path to the node, composed of subnode labels separated by spaces
     * @return The node, or <code>null</code> if not found or if this node has no children
     */
    @ApiStatus.Experimental
    public @Nullable NodeWrapper findDescendant(String path) {
        String[] split = path.split(" ", 2);
        String label = split[0];
        String remaining = split.length == 2 ? split[1] : "";

        if (isSubGroup()) {
            for (NodeWrapper child : asSubGroup().getChildNodes())
                if (child.asSubNode().label().equalsIgnoreCase(label)) {
                    if (remaining.isBlank())
                        return child;
                    else if (child.isSubGroup()) {
                        NodeWrapper found = child.findDescendant(remaining);
                        if (found != null)
                            return found;
                    }
                }

            return null;
        }

        return null;
    }

    /**
     * Generates a command usage message for this node, based on the permissions of the given user.
     * Does not send the message to the user.
     * <p>
     * Experimentally available to allow making your own help subcommand.
     * @param user The user who will see the message
     * @return The usage message
     */
    @ApiStatus.Experimental
    public String generateUsage(CommandSender user) {
        if (usageGenerator == null)
            usageGenerator = new UsageGenerator(this);
        return usageGenerator.generate(user);
    }

}
