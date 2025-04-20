package com.elvenide.core.providers;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.lang.LangKeySupplier;
import com.elvenide.core.providers.lang.LangPattern;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class LangProvider extends Provider {

    private static final HashMap<String, String> messages = new HashMap<>();

    /// A set of commonly used lang keys, many of which are used within ElvenideCore
    public CommonLangKeys common;

    @ApiStatus.Internal
    public LangProvider(@Nullable ElvenideCore core) {
        super(core);
    }

    /**
     * Gets the String representation of the MiniMessage tag for a lang key.
     * @param key The key
     * @return The tag
     * @since 0.0.8
     */
    public String tag(@LangPattern String key) {
        return "<elang:" + key + ">";
    }

    /**
     * Gets the lang key from a MiniMessage format tag.
     * @param tag The tag
     * @return The key
     * @since 0.0.9
     */
    public @LangPattern String untag(String tag) {
        @Subst("tag") String replace = tag.replace("<elang:", "").replace(">", "");
        return replace;
    }

    /**
     * Gets the current value of a lang key.
     * @param key The key
     * @return The value
     */
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
        public String setNotPlayer(String value) { return create("not_player", value); }

        /// @see #NO_PERMISSION
        public String setNoPermission(String value) { return create("no_permission", value); }

        /// @see #COMMAND_HEADER
        public String setCommandHeader(String value) { return create("command_header", value); }

        /// @see #COMMAND_USAGE_PREFIX
        public String setCommandUsagePrefix(String value) { return create("command_usage_prefix", value); }

        /// @see #MISSING_ARGUMENT
        public String setMissingArgument(String value) { return create("missing_arg", value, "%s"); }

        /// @see #INVALID_TYPE
        public String setInvalidType(String value) { return create("invalid_arg_type", value, "%s"); }

        /// @see #INVALID_PLAYER
        public String setInvalidPlayer(String value) { return create("invalid_arg_player", value, "%s"); }

        /**
         * Error message shown to non-player command senders trying to use player-only commands.
         * @since 0.0.8
         */
        public final String NOT_PLAYER = setNotPlayer("<red>You must be a player to use this.");

        /**
         * Error message shown to command senders trying to use commands they do not have permission for.
         * @since 0.0.8
         */
        public final String NO_PERMISSION = setNoPermission("<red>You do not have permission to use this.");

        /**
         * The command header, displayed above command usage information.
         * @since 0.0.8
         */
        public final String COMMAND_HEADER = setCommandHeader("<gradient:red:dark_red>ElvenideCore-Based Plugin");

        /**
         * The command usage prefix, displayed at the start of command usage information lines.
         * @since 0.0.10
         */
        public final String COMMAND_USAGE_PREFIX = setCommandUsagePrefix("<gray>");

        /**
         * Error message shown when a required argument is missing.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        public final String MISSING_ARGUMENT = setMissingArgument("Missing argument: '%s'");

        /**
         * Error message shown when an argument has an invalid datatype.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        public final String INVALID_TYPE = setInvalidType("Invalid value provided for argument: '%s'");

        /**
         * Error message shown when an argument specifies an invalid player.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        public final String INVALID_PLAYER = setInvalidPlayer("Invalid player selector/username provided for argument: '%s'");
    }

    static Tag createElangTag(final ArgumentQueue args, final Context ignored) {
        final String key = args.popOr("The <elang> tag requires at least one argument, the key to replace with a lang value.").value();
        final String value = messages.getOrDefault(key, "");

        ArrayList<String> placeholders = new ArrayList<>();
        while (args.hasNext()) {
            placeholders.add(args.pop().value());
        }

        return Tag.preProcessParsed(value.formatted((Object[]) placeholders.toArray(String[]::new)));
    }
}
