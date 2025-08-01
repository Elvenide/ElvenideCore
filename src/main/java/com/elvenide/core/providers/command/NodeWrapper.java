package com.elvenide.core.providers.command;

import com.elvenide.core.api.PublicAPI;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper around commands, subgroups, and subcommands that holds ancestry information
 * on each node's place in the ElvenideCore command tree.
 * <p></p>
 * Mostly used internally for command tree management and command usage generation.
 * @since 0.0.15
 * @author <a href="https://elvenide.com">Elvenide</a>
 */
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
    @PublicAPI
    @Contract(pure = true)
    @ApiStatus.Experimental
    public @Nullable NodeWrapper parent() {
        return parent;
    }

    @Contract(pure = true)
    SubNode asSubNode() {
        return subNode;
    }

    @Contract(pure = true)
    SubCommand asSubCommand() {
        return (SubCommand) subNode;
    }

    @Contract(pure = true)
    SubGroup asSubGroup() {
        return (SubGroup) subNode;
    }

    @Contract(pure = true)
    boolean isSubCommand() {
        return subNode instanceof SubCommand;
    }

    @Contract(pure = true)
    boolean isSubGroup() {
        return subNode instanceof SubGroup;
    }

    /**
     * Recursively gets the NodeWrapper for the given subnode further down the node tree.
     * @param node The subnode
     * @return The node wrapper, or <code>null</code> if not found
     */
    @Contract(pure = true)
    @Nullable NodeWrapper getNodeWrapper(SubNode node) {
        if (isSubGroup()) {
            for (NodeWrapper child : asSubGroup().getChildNodes())
                if (child.asSubNode() == node)
                    return child;
                else if (child.isSubGroup()) {
                    NodeWrapper found = child.getNodeWrapper(node);
                    if (found != null)
                        return found;
                }

            return null;
        }

        return null;
    }

    /**
     * Recursively gets all paths (as space-separated Strings) from this node to all descendant nodes.
     * If this node is a subcommand, it will solely return its own label.
     * <p></p>
     * Experimentally available to allow making your own help subcommand.
     * @return List of paths
     */
    @PublicAPI
    @Contract(pure = true)
    @ApiStatus.Experimental
    public @NotNull List<String> getSubPaths() {
        String currentName = parent == null ? "" : asSubNode().label() + " ";
        ArrayList<String> paths = new ArrayList<>();

        if (isSubGroup()) {
            for (NodeWrapper child : asSubGroup().getChildNodes()) {
                List<String> childPaths = child.getSubPaths();
                paths.addAll(
                    childPaths
                        .stream()
                        .map(path -> currentName + path)
                        .toList()
                );
            }
        }
        else if (!currentName.isBlank())
            paths.add(currentName.strip());

        return paths;
    }

    /**
     * Recursively finds a subnode further down the node tree using the given path (case-insensitive).
     * <p></p>
     * Experimentally available to allow making your own help subcommand.
     * @param path Path to the node, composed of subnode labels separated by spaces
     * @return The node, or <code>null</code> if not found or if this node has no children
     */
    @PublicAPI
    @Contract(pure = true)
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
     * <p></p>
     * Experimentally available to allow making your own help subcommand.
     * @param user The user who will see the message
     * @return The usage message
     */
    @PublicAPI
    @Contract(pure = true)
    @ApiStatus.Experimental
    public String generateUsage(CommandSender user) {
        if (usageGenerator == null)
            usageGenerator = new UsageGenerator(this);
        return usageGenerator.generate(user);
    }

}
