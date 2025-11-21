package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SubArgumentContext {

    final SubCommandContext sub;

    SubArgumentContext(SubCommandContext sub) {
        this.sub = sub;
    }

    private SubArgumentBuilder arg(@NotNull String name) {
        return sub.subCommandData.subArgs.stream().filter(a -> a.label.equals(name)).findFirst().orElse(null);
    }

    private <T> T getRaw(String name, Class<T> type) {
        if (!isProvided(name)) {
            if (arg(name).required)
                throw new InvalidArgumentException(Core.lang.MISSING_ARGUMENT.toString(), name);
            else
                return null;
        }

        try {
            return sub.ctx.getArgument(name, type);
        }
        catch (IllegalArgumentException ignored) {
            throw new InvalidArgumentException(Core.lang.INVALID_TYPE.toString(), name);
        }
    }

    /**
     * Checks if an optional argument was provided by the user.
     * @param name Name of the argument
     * @return Boolean
     * @since 0.0.6
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isProvided(@NotNull String name) {
        SubArgumentBuilder arg = arg(name);
        if (arg == null)
            throw new IllegalArgumentException("Unknown argument: " + name);

        int index = sub.subCommandData.subArgs.indexOf(arg);
        return index < sub.specifiedArgs;
    }

    /**
     * Checks if an argument provided by the user has a specific value.
     * @param name Name of the argument
     * @param value Expected value
     * @return Boolean
     * @param <T> Type of the argument
     * @since 0.0.6
     */
    @PublicAPI
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T> boolean isEqual(@NotNull String name, @NotNull T value) {
        T raw = (T) getRaw(name, value.getClass());
        return raw != null && raw.equals(value);
    }

    /**
     * Creates a chainable conditional that can execute code if an argument was or was not provided.
     * @param name Name of the argument
     * @return ArgumentConditional
     * @since 0.0.6
     */
    @PublicAPI
    @Contract(pure = true)
    public ArgumentConditional ifProvided(@NotNull String name) {
        return ifTrue(isProvided(name));
    }

    /**
     * Creates a chainable conditional that can execute code if an argument has a specific value.
     * @param name Name of the argument
     * @param value Expected value
     * @return ArgumentConditional
     * @since 0.0.6
     */
    @PublicAPI
    @Contract(pure = true)
    public <T> ArgumentConditional ifEqual(@NotNull String name, @NotNull T value) {
        return ifTrue(isEqual(name, value));
    }

    /**
     * Creates a chainable conditional that can execute code if a condition is true.
     * @param condition Condition
     * @return ArgumentConditional
     * @since 0.0.14
     */
    @PublicAPI
    @Contract(pure = true)
    public ArgumentConditional ifTrue(boolean condition) {
        return new ArgumentConditional(this, condition, true);
    }

    @PublicAPI
    @Contract(pure = true)
    public boolean getBool(@NotNull String name, boolean def) {
        Boolean raw = getRaw(name, Boolean.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public int getInt(@NotNull String name, int def) {
        Integer raw = getRaw(name, Integer.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public long getLong(@NotNull String name, long def) {
        Long raw = getRaw(name, Long.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public float getFloat(@NotNull String name, float def) {
        Float raw = getRaw(name, Float.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public double getDouble(@NotNull String name, double def) {
        Double raw = getRaw(name, Double.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull String getString(@NotNull String name, @NotNull String def) {
        String raw = getRaw(name, String.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull List<Player> getPlayers(@NotNull String name, @NotNull List<Player> def) {
        PlayerSelectorArgumentResolver raw = getRaw(name, PlayerSelectorArgumentResolver.class);
        if (raw == null)
            return def;

        try {
            return raw.resolve(sub.ctx.getSource());
        }
        catch (CommandSyntaxException e) {
            throw new InvalidArgumentException(Core.lang.INVALID_PLAYER.toString(), name);
        }
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull Player getPlayer(@NotNull String name, @NotNull Player def) {
        List<Player> players = getPlayers(name, List.of(def));
        if (players.isEmpty())
            return def;
        else
            return players.getFirst();
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull Material getMaterial(@NotNull String name, @NotNull Material def) {
        return getItem(name, new ItemStack(def)).getType();
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull ItemStack getItem(@NotNull String name, @NotNull ItemStack def) {
        ItemStack raw = getRaw(name, ItemStack.class);
        if (raw == null)
            return def;

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public boolean getBool(@NotNull String name) {
        Boolean raw = getRaw(name, Boolean.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public int getInt(@NotNull String name) {
        Integer raw = getRaw(name, Integer.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public long getLong(@NotNull String name) {
        Long raw = getRaw(name, Long.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public float getFloat(@NotNull String name) {
        Float raw = getRaw(name, Float.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public double getDouble(@NotNull String name) {
        Double raw = getRaw(name, Double.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull String getString(@NotNull String name) {
        String raw = getRaw(name, String.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull List<Player> getPlayers(@NotNull String name) {
        PlayerSelectorArgumentResolver raw = getRaw(name, PlayerSelectorArgumentResolver.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        try {
            return raw.resolve(sub.ctx.getSource());
        }
        catch (CommandSyntaxException e) {
            throw new InvalidArgumentException(Core.lang.INVALID_PLAYER.toString(), name);
        }
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull Player getPlayer(@NotNull String name) {
        List<Player> players = getPlayers(name);
        if (players.isEmpty())
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");
        else
            return players.getFirst();
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull Material getMaterial(@NotNull String name) {
        return getItem(name).getType();
    }

    @PublicAPI
    @Contract(pure = true)
    public @NotNull ItemStack getItem(@NotNull String name) {
        ItemStack raw = getRaw(name, ItemStack.class);
        if (raw == null)
            throw new NullPointerException("Attempted usage of ElvenideCore command argument that wasn't provided.");

        return raw;
    }

}
