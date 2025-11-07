package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.mojang.brigadier.arguments.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class SubArgumentBuilder {

    final String label;
    final ArgumentType<?> type;
    boolean required = true;
    @Nullable Function<SubCommandContext, List<String>> suggester = null;
    private int specialType;

    SubArgumentBuilder(String label, ArgumentType<?> type) {
        this.label = label;
        this.type = type;
    }

    SubArgumentBuilder(String label, ArgumentType<?> type, int specialType) {
        this(label, type);
        this.specialType = specialType;
    }

    /**
     * Sets this argument as not required.
     * @return This
     * @since 0.0.8
     */
    @PublicAPI
    public SubArgumentBuilder setOptional() {
        this.required = false;
        return this;
    }

    /**
     * Allows you to build a dynamic tab-completion list for this argument.
     * @param suggester Suggestion builder function
     * @since 0.0.8
     */
    @PublicAPI
    public void suggests(@NotNull Function<SubCommandContext, List<String>> suggester) {
        this.suggester = suggester;
    }

    /**
     * Allows you to build a static tab-completion list for this argument.
     * @param staticList List of suggestions
     * @since 0.0.13
     */
    @PublicAPI
    public void suggests(List<String> staticList) {
        this.suggester = context -> staticList;
    }

    /**
     * Allows you to build a static tab-completion list for this argument.
     * @param staticArray String suggestions
     * @since 0.0.14
     */
    @PublicAPI
    public void suggests(String... staticArray) {
        this.suggester = context -> List.of(staticArray);
    }

    boolean isBool() {
        return type instanceof BoolArgumentType;
    }

    boolean isNumeric() {
        return type instanceof IntegerArgumentType || type instanceof LongArgumentType || type instanceof FloatArgumentType || type instanceof DoubleArgumentType;
    }

    boolean isPlayer() {
        return specialType == 1 || specialType == 2;
    }

    boolean isItem() {
        return specialType == 3 || specialType == 4;
    }

    String formatted() {
        String output = "";

        String openBracket = required ? "<" : "[";
        String closeBracket = required ? ">" : "]";
        String type = getTypeName();

        // Open bracket + argument name
        output += openBracket + label;

        // + argument type (unless equal to argument name)
        if (!type.equalsIgnoreCase(label))
            output += ": " + type;

        // + close bracket
        output += closeBracket;

        if (isBool())
            return Core.lang.BOOL_ARGUMENT_HELP_FORMATTING.get(output);

        if (isNumeric())
            return Core.lang.NUMBER_ARGUMENT_HELP_FORMATTING.get(output);

        if (isPlayer())
            return Core.lang.PLAYER_ARGUMENT_HELP_FORMATTING.get(output);

        if (isItem())
            return Core.lang.ITEM_ARGUMENT_HELP_FORMATTING.get(output);

        return Core.lang.STRING_ARGUMENT_HELP_FORMATTING.get(output);
    }

    String getTypeName() {
        if (type instanceof BoolArgumentType) return "true/false";
        if (type instanceof IntegerArgumentType) return "integer";
        if (type instanceof LongArgumentType) return "integer"; // users will understand "integer" better than "long"
        if (type instanceof FloatArgumentType) return "decimal"; // users will understand "decimal" better than "float"
        if (type instanceof DoubleArgumentType) return "decimal"; // users will understand "decimal" better than "double"
        if (type instanceof StringArgumentType stringType) {
            if (stringType.getType() == StringArgumentType.StringType.SINGLE_WORD) return "text with no spaces";
            if (stringType.getType() == StringArgumentType.StringType.QUOTABLE_PHRASE) return "any text within quotes";
            if (stringType.getType() == StringArgumentType.StringType.GREEDY_PHRASE) return "any text";
        }
        if (specialType == 1) return "player";
        if (specialType == 2) return "players";
        if (specialType == 3) return "item type";
        if (specialType == 4) return "item";

        return "unknown";
    }
}
