package com.elvenide.core.providers.lang;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#lang} field.
 */
public class LangProvider extends Provider {

    private static final HashMap<String, String> messages = new HashMap<>();

    /// A set of commonly used lang keys, many of which are used within ElvenideCore
    public CommonLangKeys common;

    @ApiStatus.Internal
    public LangProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Gets the String-like representation of the MiniMessage tag for a lang key.
     * @param key The key
     * @return The tag
     * @since 0.0.15
     */
    @Contract(pure = true)
    public LangKey tag(@LangPattern String key) {
        return new LangKey("<elang:" + key + ">");
    }

    /**
     * Gets the lang key from a MiniMessage format tag.
     * @param tag The tag
     * @return The key
     * @since 0.0.9
     */
    @Contract(pure = true)
    public @LangPattern String untag(String tag) {
        @Subst("tag") String replace = tag.replace("<elang:", "").replace(">", "");
        return replace;
    }

    /**
     * Gets the current value of a lang key.
     * @param key The key
     * @return The value
     */
    @Contract(pure = true)
    public String get(@LangPattern String key) {
        return messages.get(key);
    }

    /**
     * Sets the value of a lang key.
     * The key must match the tag regex pattern: <b>[a-z0-9_.]+</b>
     * <p></p>
     * The key can then be used in text using the custom MiniMessage tag <code>&lt;elang:{key}&gt;</code>, example:
     * <code>&lt;elang:no_permission&gt;</code>
     * @param key The key, containing only lowercase letters, numbers, underscores, and periods
     * @param value The value, can be any text including built-in or custom MiniMessage tags
     */
    public void set(@LangPattern String key, String value) {
        if (!key.matches("[a-z0-9_.]+"))
            throw new IllegalArgumentException("Invalid key: " + key + "; must match regex: [a-z0-9_.]+");

        messages.put(key, value);
    }

    public static class CommonLangKeys implements LangKeySupplier {
        @ApiStatus.Internal
        public CommonLangKeys() {}

        /// @see #NOT_PLAYER
        public LangKey setNotPlayer(String value) { return create("not_player", value); }

        /// @see #NO_PERMISSION
        public LangKey setNoPermission(String value) { return create("no_permission", value); }

        /// @see #COMMAND_HEADER
        public LangKey setCommandHeader(String value) { return create("command_header", value); }

        /// @see #COMMAND_USAGE_PREFIX
        public LangKey setCommandUsagePrefix(String value) { return create("command_usage_prefix", value); }

        /// @see #MISSING_ARGUMENT
        public LangKey setMissingArgument(String value) { return create("missing_arg", value, "%s"); }

        /// @see #INVALID_TYPE
        public LangKey setInvalidType(String value) { return create("invalid_arg_type", value, "%s"); }

        /// @see #INVALID_PLAYER
        public LangKey setInvalidPlayer(String value) { return create("invalid_arg_player", value, "%s"); }

        /// @see #SUBGROUP_HELP_FORMATTING
        public LangKey setSubGroupHelpColor(String value) {  return create("subgroup_help_color", value, "{}"); }

        /// @see #SUBCOMMAND_HELP_FORMATTING
        public LangKey setSubCommandHelpColor(String value) {  return create("subcommand_help_color", value, "{}"); }

        /// @see #BOOL_ARGUMENT_HELP_FORMATTING
        public LangKey setBoolArgumentHelpColor(String value) {  return create("bool_argument_help_color", value, "{}"); }

        /// @see #STRING_ARGUMENT_HELP_FORMATTING
        public LangKey setStringArgumentHelpColor(String value) {  return create("string_argument_help_color", value, "{}"); }

        /// @see #NUMBER_ARGUMENT_HELP_FORMATTING
        public LangKey setNumberArgumentHelpColor(String value) {  return create("number_argument_help_color", value, "{}"); }

        /// @see #PLAYER_ARGUMENT_HELP_FORMATTING
        public LangKey setPlayerArgumentHelpColor(String value) {  return create("player_argument_help_color", value, "{}"); }

        /// @see #ITEM_ARGUMENT_HELP_FORMATTING
        public LangKey setItemArgumentHelpColor(String value) {  return create("item_argument_help_color", value, "{}"); }

        /// @see #COMMANDS_HIDDEN_BY_PERMS
        public LangKey setCommandsHiddenByPerms(String value) { return create("commands_hidden_by_perms", value, "{}"); }

        /**
         * Error message shown to non-player command senders trying to use player-only commands.
         * @since 0.0.8
         */
        public final LangKey NOT_PLAYER = setNotPlayer("<red>You must be a player to use this.");

        /**
         * Error message shown to command senders trying to use commands they do not have permission for.
         * @since 0.0.8
         */
        public final LangKey NO_PERMISSION = setNoPermission("<red>You do not have permission to use this.");

        /**
         * The command header, displayed above command usage information.
         * @since 0.0.8
         */
        public final LangKey COMMAND_HEADER = setCommandHeader("<gradient:red:dark_red>ElvenideCore-Based Plugin");

        /**
         * The command usage prefix, displayed at the start of each command help line.
         * Can be used to color the slash (/) in the command usage display.
         * @since 0.0.10
         */
        public final LangKey COMMAND_USAGE_PREFIX = setCommandUsagePrefix("<gray>");

        /**
         * Error message shown when a required argument is missing.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        public final LangKey MISSING_ARGUMENT = setMissingArgument("Missing argument: '%s'. Hover to see command syntax.");

        /**
         * Error message shown when an argument has an invalid datatype.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        public final LangKey INVALID_TYPE = setInvalidType("Invalid value provided for argument: '%s'. Hover to see command syntax.");

        /**
         * Error message shown when an argument specifies an invalid player.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        public final LangKey INVALID_PLAYER = setInvalidPlayer("Invalid player selector/username provided for argument: '%s'. Hover to see command syntax.");

        /**
         * The formatting of subgroups and base-level commands in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey SUBGROUP_HELP_FORMATTING = setSubGroupHelpColor("<gray>{}</gray>");

        /**
         * The formatting of subcommands in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey SUBCOMMAND_HELP_FORMATTING = setSubCommandHelpColor("<gray>{}</gray>");

        /**
         * The formatting of boolean arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey BOOL_ARGUMENT_HELP_FORMATTING = setBoolArgumentHelpColor("<gold>{}</gold>");

        /**
         * The formatting of string and unknown arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey STRING_ARGUMENT_HELP_FORMATTING = setStringArgumentHelpColor("<dark_green>{}</dark_green>");

        /**
         * The formatting of numeric arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey NUMBER_ARGUMENT_HELP_FORMATTING = setNumberArgumentHelpColor("<dark_aqua>{}</dark_aqua>");

        /**
         * The formatting of player arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey PLAYER_ARGUMENT_HELP_FORMATTING = setPlayerArgumentHelpColor("<dark_purple>{}</dark_purple>");

        /**
         * The formatting of item arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey ITEM_ARGUMENT_HELP_FORMATTING = setItemArgumentHelpColor("<blue>{}</blue>");

        /**
         * Message shown when commands are hidden in the help message due to missing permissions.
         * Has a single Integer placeholder ({}).
         * @since 0.0.15
         */
        public final LangKey COMMANDS_HIDDEN_BY_PERMS = setCommandsHiddenByPerms("<dark_gray>{} commands were hidden due to lack of permissions.");
    }

}
