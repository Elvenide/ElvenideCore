package com.elvenide.core.providers.command;

import com.elvenide.core.api.PublicAPI;
import com.mojang.brigadier.arguments.*;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.function.Consumer;

public class SubCommandBuilder {

    final LinkedList<SubArgumentBuilder> subArgs;
    @Nullable String description = null;
    @Nullable String permission = null;
    boolean playerOnly = false;

    SubCommandBuilder() {
        this.subArgs = new LinkedList<>();
    }

    /* <editor-fold defaultstate="collapsed" desc="Subcommand Properties"> */

    /**
     * Sets the description of this subcommand.
     * @param description Description
     * @return This
     * @since 0.0.13
     */
    @PublicAPI
    public SubCommandBuilder setDescription(@NotNull String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the permission required to use this subcommand.
     * @param permission Permission
     * @return This
     * @since 0.0.13
     */
    @PublicAPI
    public SubCommandBuilder setPermission(@NotNull String permission) {
        this.permission = permission;
        return this;
    }

    /**
     * Sets this subcommand as player-only (console will not be able to use it).
     * @return This
     * @since 0.0.13
     */
    @PublicAPI
    public SubCommandBuilder setPlayerOnly() {
        this.playerOnly = true;
        return this;
    }

    /* </editor-fold> */

    /* <editor-fold defaultstate="collapsed" desc="Arguments With Builder Consumer"> */

    /**
     * Adds a boolean argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addBool(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, BoolArgumentType.bool());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds an integer argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addInt(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, IntegerArgumentType.integer());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds an integer argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addInt(String label, int min, int max, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, IntegerArgumentType.integer(min, max));
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a long argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addLong(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, LongArgumentType.longArg());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a long argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addLong(String label, long min, long max, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, LongArgumentType.longArg(min, max));
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a float argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addFloat(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, FloatArgumentType.floatArg());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a float argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addFloat(String label, float min, float max, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, FloatArgumentType.floatArg(min, max));
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a double argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addDouble(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, DoubleArgumentType.doubleArg());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a double argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addDouble(String label, double min, double max, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, DoubleArgumentType.doubleArg(min, max));
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a word argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addWord(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, StringArgumentType.word());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a string argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addString(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, StringArgumentType.string());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a greedy string argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addGreedy(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, StringArgumentType.greedyString());
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a player argument to the subcommand. Can represent a username or single player selector (e.g. @p, @s).
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addPlayer(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.player(), 1);
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a multi-player argument to the subcommand. Can represent a username or any player selector (e.g. @p, @s, @a).
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addPlayers(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.players(), 2);
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds a material argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addMaterial(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.itemStack(), 3);
        consumer.accept(builder);
        return add(builder);
    }

    /**
     * Adds an item argument to the subcommand.
     * @param label Name of the argument
     * @param consumer Builder function for additional argument properties
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addItem(String label, Consumer<SubArgumentBuilder> consumer) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.itemStack(), 4);
        consumer.accept(builder);
        return add(builder);
    }

    /* </editor-fold> */

    /* <editor-fold defaultState="collapsed" desc="Arguments Without Builder Consumer"> */

    /**
     * Adds a boolean argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addBool(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, BoolArgumentType.bool());
        return add(builder);
    }

    /**
     * Adds an integer argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addInt(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, IntegerArgumentType.integer());
        return add(builder);
    }

    /**
     * Adds an integer argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addInt(String label, int min, int max) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, IntegerArgumentType.integer(min, max));
        return add(builder);
    }

    /**
     * Adds a long argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addLong(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, LongArgumentType.longArg());
        return add(builder);
    }

    /**
     * Adds a long argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addLong(String label, long min, long max) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, LongArgumentType.longArg(min, max));
        return add(builder);
    }

    /**
     * Adds a float argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addFloat(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, FloatArgumentType.floatArg());
        return add(builder);
    }

    /**
     * Adds a float argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addFloat(String label, float min, float max) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, FloatArgumentType.floatArg(min, max));
        return add(builder);
    }

    /**
     * Adds a double argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addDouble(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, DoubleArgumentType.doubleArg());
        return add(builder);
    }

    /**
     * Adds a double argument to the subcommand with a defined range.
     * @param label Name of the argument
     * @param min Minimum value of the argument
     * @param max Maximum value of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addDouble(String label, double min, double max) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, DoubleArgumentType.doubleArg(min, max));
        return add(builder);
    }

    /**
     * Adds a word argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addWord(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, StringArgumentType.word());
        return add(builder);
    }

    /**
     * Adds a string argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addString(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, StringArgumentType.string());
        return add(builder);
    }

    /**
     * Adds a greedy string argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addGreedy(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, StringArgumentType.greedyString());
        return add(builder);
    }

    /**
     * Adds a player argument to the subcommand. Can represent a username or single player selector (e.g. @p, @s).
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addPlayer(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.player(), 1);
        return add(builder);
    }

    /**
     * Adds a multi-player argument to the subcommand. Can represent a username or any player selector (e.g. @p, @s, @a).
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addPlayers(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.players(), 2);
        return add(builder);
    }

    /**
     * Adds a material argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addMaterial(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.itemStack(), 3);
        return add(builder);
    }

    /**
     * Adds an item argument to the subcommand.
     * @param label Name of the argument
     * @return This
     */
    @PublicAPI
    public SubCommandBuilder addItem(String label) {
        SubArgumentBuilder builder = new SubArgumentBuilder(label, ArgumentTypes.itemStack(), 4);
        return add(builder);
    }

    /* </editor-fold> */

    private SubCommandBuilder add(SubArgumentBuilder builder) {
        this.subArgs.add(builder);
        return this;
    }

}
