package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class SubCommandContext {

    final CommandContext<CommandSourceStack> ctx;
    final SubCommandBuilder subCommandData;
    final int specifiedArgs;
    private final CommandBuilder root;

    /// The parsed arguments of the subcommand.
    @PublicAPI
    public final SubArgumentContext args;

    /**
     * Custom lambda-usable variable management.
     * <p>
     * Deprecated in favor of Java's built-in atomic objects, which are just as easy to use while providing extra features.
     * @deprecated Use atomic objects instead (e.g. {@link java.util.concurrent.atomic.AtomicInteger AtomicInteger}, {@link java.util.concurrent.atomic.AtomicReference AtomicReference})
     */
    @PublicAPI
    @Deprecated(since = "0.0.15", forRemoval = true)
    public final Variables vars = new Variables();

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
        executor().sendMessage(Core.text.deserialize(message, optionalPlaceholders));
    }

    /**
     * Sends a message specifically to the sender (and not the executor).
     * @param message String in MiniMessage format
     * @param optionalPlaceholders Optional placeholders
     */
    @PublicAPI
    public void replyToSender(Object message, Object... optionalPlaceholders) {
        ctx.getSource().getSender().sendMessage(Core.text.deserialize(message, optionalPlaceholders));
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
     * Sends command help information to the sender (and not the executor).
     * <p>
     * <i>Deprecated in favor of direct access to the underlying NodeWrapper implementation that
     * generates the command usage message.</i>
     * @deprecated Use {@link #getCommandTreeNode(SubCommand) getCommandTreeNode(null).generateUsage(CommandSender)}
     *             to get the root command usage message
     */
    @PublicAPI
    @Deprecated(forRemoval = true, since = "0.0.15")
    public void sendCommandUsage() {
        replyToSender(getCommandTreeNode(null).generateUsage(ctx.getSource().getSender()));
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

    /// See {@link SubCommandContext#vars} for deprecation reason.
    @Deprecated(since = "0.0.15", forRemoval = true)
    public static class Variables {

        private static final HashMap<Class<?>, Class<?>> PRIMITIVES = new HashMap<>();

        static {
            PRIMITIVES.put(boolean.class, Boolean.class);
            PRIMITIVES.put(int.class, Integer.class);
            PRIMITIVES.put(long.class, Long.class);
            PRIMITIVES.put(float.class, Float.class);
            PRIMITIVES.put(double.class, Double.class);
            PRIMITIVES.put(short.class, Short.class);
            PRIMITIVES.put(byte.class, Byte.class);
            PRIMITIVES.put(char.class, Character.class);
        }

        private final HashMap<String, Object> vars = new HashMap<>();

        private Variables() {}

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        public <T> void set(String key, T value) {
            vars.put(key, value);
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        @SuppressWarnings("unchecked")
        public <T> T get(String key, Class<T> type) {
            final Object result = vars.get(key);
            if (PRIMITIVES.getOrDefault(type, type).isAssignableFrom(result.getClass()))
                return (T) result;
            return null;
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        @SuppressWarnings("unchecked")
        public <T> T get(String key, T def) {
            final Object result = vars.getOrDefault(key, def);
            if (PRIMITIVES.getOrDefault(def.getClass(), def.getClass()).isAssignableFrom(result.getClass()))
                return (T) result;
            return def;
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        public void add(String key, int value) {
            set(key, get(key, 0) + value);
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        public void add(String key, double value) {
            set(key, get(key, 0D) + value);
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        public void add(String key, long value) {
            set(key, get(key, 0L) + value);
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        public void add(String key, float value) {
            set(key, get(key, 0F) + value);
        }

        /// See {@link SubCommandContext#vars} for deprecation reason.
        @Deprecated(since = "0.0.15", forRemoval = true)
        public void add(String key, String value) {
            set(key, get(key, "") + value);
        }

    }

}
