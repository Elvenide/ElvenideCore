package com.elvenide.core.providers.commands;

import com.elvenide.core.ElvenideCore;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SubCommandContext {

    final CommandContext<CommandSourceStack> ctx;
    final SubCommandBuilder subCommandData;
    final int specifiedArgs;
    private final CommandBuilder root;

    /// The parsed arguments of the subcommand.
    public final SubArgumentContext args;

    /// Custom lambda-usable variable management.
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
    public @NotNull Player player() {
        return (Player) executor();
    }

    /**
     * Gets the location the command is being executed at, usually the location of the executing player.
     * @return Location
     */
    public Location location() {
        return ctx.getSource().getLocation();
    }

    /**
     * Checks if the executor has a permission. Supports normal and negated permissions.
     * @param permission Permission
     * @return Boolean
     */
    public boolean hasPermission(String permission) {
        return ElvenideCore.perms.has(executor(), permission);
    }

    /**
     * Checks if the executor is a player.
     * @return Boolean
     */
    public boolean isPlayer() {
        return executor() instanceof Player;
    }

    /**
     * Sends a message to the executor or sender.
     * @param message String in MiniMessage format
     * @param optionalPlaceholders Optional placeholders
     */
    public void reply(String message, Object... optionalPlaceholders) {
        executor().sendMessage(ElvenideCore.text.deserialize(message, optionalPlaceholders));
    }

    /**
     * Sends a message specifically to the sender (and not the executor).
     * @param message String in MiniMessage format
     * @param optionalPlaceholders Optional placeholders
     */
    public void replyToSender(String message, Object... optionalPlaceholders) {
        ctx.getSource().getSender().sendMessage(ElvenideCore.text.deserialize(message, optionalPlaceholders));
    }

    /**
     * Sends command help information to the sender (and not the executor).
     */
    public void sendCommandUsage() {
        replyToSender(root.getUsage(null));
    }

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

        public <T> void set(String key, T value) {
            vars.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key, Class<T> type) {
            final Object result = vars.get(key);
            if (PRIMITIVES.getOrDefault(type, type).isAssignableFrom(result.getClass()))
                return (T) result;
            return null;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key, T def) {
            final Object result = vars.getOrDefault(key, def);
            if (PRIMITIVES.getOrDefault(def.getClass(), def.getClass()).isAssignableFrom(result.getClass()))
                return (T) result;
            return def;
        }

        public void add(String key, int value) {
            set(key, get(key, 0) + value);
        }

        public void add(String key, double value) {
            set(key, get(key, 0D) + value);
        }

        public void add(String key, long value) {
            set(key, get(key, 0L) + value);
        }

        public void add(String key, float value) {
            set(key, get(key, 0F) + value);
        }

        public void add(String key, String value) {
            set(key, get(key, "") + value);
        }

    }

}
