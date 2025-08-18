package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SubCommandContext {

    final CommandContext<CommandSourceStack> ctx;
    final SubCommandBuilder subCommandData;
    final int specifiedArgs;
    private final CommandBuilder root;

    /// The parsed arguments of the subcommand.
    @PublicAPI
    public final SubArgumentContext args;

    SubCommandContext(CommandContext<CommandSourceStack> ctx, SubCommandBuilder subCommandData, CommandBuilder root, int specifiedArgs) {
        this.ctx = ctx;
        this.subCommandData = subCommandData;
        this.args = new SubArgumentContext(this);
        this.root = root;
        this.specifiedArgs = specifiedArgs;
    }

    /**
     * Gets the command executor if one exists, or otherwise gets the command sender.
     * @return The executor or sender
     */
    @PublicAPI
    @Contract(pure = true)
    public @NotNull CommandSender executor() {
        Entity e = ctx.getSource().getExecutor();
        if (e == null)
            return ctx.getSource().getSender();
        return e;
    }

    /**
     * Gets the executor as a player. Check if the executor is a player first with <code>isPlayer()</code>.
     * @return The executing player
     */
    @PublicAPI
    @Contract(pure = true)
    public @NotNull Player player() {
        return (Player) executor();
    }

    /**
     * Gets the location the command is being executed at, usually the location of the executing player.
     * @return Location
     */
    @PublicAPI
    @Contract(pure = true)
    public Location location() {
        return ctx.getSource().getLocation();
    }

    /**
     * Checks if the executor has a permission. Supports normal and negated permissions.
     * @param permission Permission
     * @return Boolean
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean hasPermission(String permission) {
        return Core.perms.has(executor(), permission);
    }

    /**
     * Checks if the executor is a player.
     * @return Boolean
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isPlayer() {
        return executor() instanceof Player;
    }

    /**
     * Sends a message to the executor or sender.
     * @param message String in MiniMessage format
     * @param optionalPlaceholders Optional placeholders
     */
    @PublicAPI
    public void reply(Object message, Object... optionalPlaceholders) {
        Core.text.send(executor(), message, optionalPlaceholders);
    }

    /**
     * Sends a message specifically to the sender (and not the executor).
     * @param message String in MiniMessage format
     * @param optionalPlaceholders Optional placeholders
     */
    @PublicAPI
    public void replyToSender(Object message, Object... optionalPlaceholders) {
        Core.text.send(ctx.getSource().getSender(), message, optionalPlaceholders);
    }

    /**
     * Ends the command with an error message.
     * <p>
     * Prevents execution of any command code after the method call, and immediately sends the error message to the sender.
     * @param errorMessage Error message
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public void end(Object errorMessage, Object... optionalPlaceholders) {
        throw new InvalidArgumentException("%s", Core.text.format(errorMessage, optionalPlaceholders));
    }

    /**
     * Performs a command as console.
     * @param command Command (no leading slash)
     * @since 0.0.14
     */
    @PublicAPI
    public void performConsoleCommand(String command) {
        executor().getServer().dispatchCommand(executor().getServer().getConsoleSender(), command);
    }

    /**
     * Gets the plugin that registered this subcommand.
     * @return JavaPlugin instance
     * @since 0.0.16
     */
    @PublicAPI
    public JavaPlugin plugin() {
        return Core.plugin.get();
    }

    /**
     * Provides access to information on the subcommand's location in the command tree,
     * and allows traversing the command tree from the subcommand's location.
     * <p>
     * Experimentally available primarily for the niche use-case of making your own help subcommand.
     * @param subCommand Your subcommand (if null, the root command node will be used)
     * @return NodeWrapper with command tree info, or <code>null</code> if not found
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(value = "null -> !null", pure = true)
    @ApiStatus.Experimental
    public @Nullable NodeWrapper getCommandTreeNode(@Nullable SubCommand subCommand) {
        if (subCommand == null)
            return root.commandNode;
        return root.commandNode.getNodeWrapper(subCommand);
    }

}
