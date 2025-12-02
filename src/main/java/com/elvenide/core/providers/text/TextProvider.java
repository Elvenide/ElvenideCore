package com.elvenide.core.providers.text;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.lang.LangKey;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#text} field.
 */
public class TextProvider extends Provider {
    private static MiniMessage miniMessage;
    private static final TagResolver.Builder customResolver = TagResolver.builder();
    private static final HashMap<String, String> customColorTags = new HashMap<>();
    private static final HashMap<String, String> customTextTags = new HashMap<>();

    /// Flag that handles whether &lt;gradient&gt; tags should be auto-converted to &lt;egradient&gt;
    @PublicAPI
    private static boolean autoConvertGradientToEgradient = true;

    /// Flag that handles whether &lt;shadow&gt; tags should be auto-converted to &lt;eshadow&gt;
    @PublicAPI
    private static boolean autoConvertShadowToEshadow = true;

    /// A set of built-in text packages that add additional custom tags to MiniMessage
    public final PackageManager packages = new PackageManager();

    @ApiStatus.Internal
    public TextProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Sets the flag that handles whether &lt;gradient&gt; tags should be auto-converted to &lt;egradient&gt;
     * @param value Boolean value
     * @since 0.0.15
     */
    @PublicAPI
    public static void shouldAutoConvertGradientToEgradient(boolean value) {
        autoConvertGradientToEgradient = value;
    }

    /**
     * Sets the flag that handles whether &lt;shadow&gt; tags should be auto-converted to &lt;eshadow&gt;
     * @param value Boolean value
     * @since 0.0.15
     */
    @PublicAPI
    public static void shouldAutoConvertShadowToEshadow(boolean value) {
        autoConvertShadowToEshadow = value;
    }

    /**
     * ElvenideCore's custom MiniMessage instance, with support for custom tags/colors.
     * <p>
     * Note: Intended for internal use, and as such is not stable API and should not be used unless absolutely necessary.
     * @return MiniMessage
     */
    @PublicAPI
    @ApiStatus.Experimental
    public static MiniMessage resolver() {
        if (miniMessage != null)
            return miniMessage;

        miniMessage = MiniMessage.builder()
                .editTags(builder ->
                    builder.resolver(StandardTags.defaults())
                        .tag("egradient", TextProvider::createEgradientTag)
                        .tag("escape", TextProvider::createEscapeTag)
                        .tag("eshadow", TextProvider::createEshadowTag)
                )
                .build();
        return miniMessage;
    }

    private static String convertLegacyToMiniMessage(String text, String prefix) {
        return text
                .replace(prefix + "0", "<black>")
                .replace(prefix + "1", "<dark_blue>")
                .replace(prefix + "2", "<dark_green>")
                .replace(prefix + "3", "<dark_aqua>")
                .replace(prefix + "4", "<dark_red>")
                .replace(prefix + "5", "<dark_purple>")
                .replace(prefix + "6", "<gold>")
                .replace(prefix + "7", "<gray>")
                .replace(prefix + "8", "<dark_gray>")
                .replace(prefix + "9", "<blue>")
                .replaceAll(prefix + "[aA]", "<green>")
                .replaceAll(prefix + "[bB]", "<aqua>")
                .replaceAll(prefix + "[cC]", "<red>")
                .replaceAll(prefix + "[dD]", "<light_purple>")
                .replaceAll(prefix + "[eE]", "<yellow>")
                .replaceAll(prefix + "[fF]", "<white>")
                .replaceAll(prefix + "[kK]", "<obfuscated>")
                .replaceAll(prefix + "[lL]", "<bold>")
                .replaceAll(prefix + "[mM]", "<strikethrough>")
                .replaceAll(prefix + "[nN]", "<underlined>")
                .replaceAll(prefix + "[oO]", "<italic>")
                .replaceAll(prefix + "[rR]", "<reset>")
                .replaceAll(prefix + "(#[a-fA-F0-9]{6})", "<$1>");
    }

    /// @since 0.0.1
    private static String convertLegacyToMiniMessage(String text) {
        return convertLegacyToMiniMessage(
                convertLegacyToMiniMessage(text, "§"),
                "&");
    }

    /// @since 0.0.13
    static String preParsing(String text, Object[] placeholders) {
        // Auto-convert <gradient> to <egradient>
        if (autoConvertGradientToEgradient)
            text = text.replace("<gradient:", "<egradient:")
                .replace("</egradient>", "</gradient>");

        // Auto-convert <shadow> to <eshadow>
        if (autoConvertShadowToEshadow)
            text = text.replace("<shadow:", "<eshadow:")
                .replace("</eshadow>", "</shadow>");

        // Format placeholders
        if (placeholders.length > 0)
            text = Core.text.format(text, placeholders);

        // Convert legacy colors
        return convertLegacyToMiniMessage(text);
    }

    /// @since 0.0.2
    private static TagResolver createCustomColorResolver(@TagPattern String name, String color) {
        if (customColorTags.containsKey(name) || customTextTags.containsKey(name))
            throw new IllegalArgumentException("Tag name already in use: " + name);

        customColorTags.put(name, color);
        return TagResolver.resolver(name, Tag.styling(
                Objects.requireNonNull(TextColor.fromHexString(color))
        ));
    }

    /// @since 0.0.18
    private static TagResolver createCustomTextResolver(@TagPattern String name, String text) {
        if (customColorTags.containsKey(name) || customTextTags.containsKey(name))
            throw new IllegalArgumentException("Tag name already in use: " + name);

        text = preParsing(text, new Object[0]);
        customTextTags.put(name, text);
        return TagResolver.resolver(name, Tag.preProcessParsed(text));
    }

    /// @since 0.0.11
    private static Tag createEgradientTag(final ArgumentQueue args, final Context ignored) {
        final String first = args.popOr("The <egradient> tag requires at least two color arguments; none were provided.").value();
        final String second = args.popOr("The <egradient> tag requires at least two color arguments; only one was provided.").value();

        StringBuilder value = new StringBuilder("<gradient:")
            .append(customColorTags.getOrDefault(first, first)).append(":")
            .append(customColorTags.getOrDefault(second, second));

        while (args.hasNext()) {
            String arg = args.pop().value();
            value.append(":").append(customColorTags.getOrDefault(arg, arg));
        }
        value.append(">");

        return Tag.preProcessParsed(value.toString());
    }

    /// @since 0.0.12
    private static Tag createEscapeTag(final ArgumentQueue args, final Context ignored) {
        StringBuilder value = new StringBuilder(args.popOr("The <escape> tag requires exactly one argument, the text to escape.").value());
        while (args.hasNext()) {
            String arg = args.pop().value();
            value.append(":").append(arg);
        }

        return Tag.selfClosingInserting(PlainTextComponentSerializer.plainText().deserialize(value.toString()));
    }

    /// @since 0.0.15
    private static Tag createEshadowTag(final ArgumentQueue args, final Context ignored) {
        final String color = args.popOr("The <eshadow> tag requires a color argument.").value();

        StringBuilder value = new StringBuilder("<shadow:")
            .append(customColorTags.getOrDefault(color, color));

        if (args.hasNext()) {
            String opacity = args.pop().value();
            value.append(":").append(opacity);
        }
        value.append(">");

        return Tag.preProcessParsed(value.toString());
    }

    /**
     * Converts any object to a string.
     * <p>
     * If the object is a {@link LangKey}, it will return the key's value.<br/>
     * Otherwise, it will return the object's toString() value or "null".<br/>
     * @param rawText The object to convert
     * @return The string representation of the object
     * @since 0.0.17
     */
    public final @NotNull String valueOf(@Nullable Object rawText) {
        // Stringify lang keys
        if (rawText instanceof LangKey key)
            return key.get();

        // Stringify arrays of any type
        if (rawText != null && rawText.getClass().isArray()) {
            // Convert char arrays into a String made up of those chars
            if (rawText instanceof char[] chars)
                return String.valueOf(chars);

            String output = Arrays.deepToString(new Object[]{rawText});
            return output.substring(1, output.length() - 1);
        }
        return String.valueOf(rawText);
    }

    /**
     * Formats text with any number of placeholders.<br/>
     * Supports all Java format placeholders (e.g. %s, %d).<br/>
     * Supports custom placeholders (e.g. {}).
     * <p>
     * Example (returns "Hello, world!"):<br/>
     * <code>
     *     format("%s, {}!", "Hello", "world");
     * </code>
     * @param rawText The text to format
     * @param placeholders The placeholders to insert in the text
     * @return The formatted text
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String format(@Nullable Object rawText, @Nullable Object... placeholders) {
        String text = valueOf(rawText);

        // Return text if there are no placeholders
        if (placeholders.length == 0)
            return text;

        // Find java and custom placeholders
        Pattern pattern = Pattern.compile("%([0-9]+[$]|<)*[-#+ 0,(]*[0-9]*([.][0-9]+)?[nbBhHsScCfdoxXeEgGaA]|%[tT][a-zA-Z]|\\{}");
        Matcher matcher = pattern.matcher(text);

        int i = 0;
        char[] output = text.toCharArray();
        while (matcher.find()) {
            String placeholder = matcher.group();

            // Keep java placeholders
            if (!placeholder.equals("{}")) {
                i++;
                continue;
            }

            // Convert custom placeholders to java placeholders

            if (i > placeholders.length)
                break;

            char formatLetter = 's';
            if (placeholders[i] instanceof Integer || placeholders[i] instanceof Long)
                formatLetter = 'd';
            else if (placeholders[i] instanceof Float || placeholders[i] instanceof Double)
                formatLetter = 'f';
            else if (placeholders[i] instanceof Boolean)
                formatLetter = 'b';
            else if (placeholders[i] instanceof Character)
                formatLetter = 'c';
            else if (!(placeholders[i] instanceof String))
                placeholders[i] = valueOf(placeholders[i]);

            output[matcher.start()] = '%';
            output[matcher.start() + 1] = formatLetter;
            i++;
        }

        // Format text using java placeholder system
        return valueOf(output).formatted(placeholders);
    }

    /**
     * Serializes a MiniMessage Component to a String in MiniMessage format.
     * Automatically escapes any tags present within the component.
     * <p>
     * Supports custom ElvenideCore tags:
     * <ul>
     *     <li>Custom color tags created by you with {@link #addColorTag(String, String)}</li>
     *     <li><code>&lt;egradient:{color1}:{color2...}:[phase]&gt;</code> tags that support your custom colors</li>
     *     <li><code>&lt;escape:'{text}'&gt;</code> tags that escape any MiniMessage tags in them</li>
     *     <li><code>&lt;eshadow:{color}:[opacity]&gt;</code> tags that support your custom colors</li>
     * </ul>
     * @param component The component
     * @return Serialized text
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String toString(@NotNull Component component) {
        return resolver().serialize(component);
    }

    /**
     * Serializes a MiniMessage Component to a plain text String without component tags.
     * @param component The component
     * @return Serialized text
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String toPlainString(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Serializes a MiniMessage Component to a legacy text String (with <code>§</code> codes).
     * @param component The component
     * @return Serialized text
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String toLegacyString(@NotNull Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    /**
     * Converts a string to title case.
     * <p>
     * Example: <code>hello world</code> -> <code>Hello World</code>
     * @param text The string
     * @return The title-cased string
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String toTitleCase(@NotNull String text) {
        if (text.isEmpty())
            return text;

        if (text.length() == 1)
            return text.toUpperCase();

        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Deserializes a String in MiniMessage format to a MiniMessage Component.
     * <p>
     * Supports custom ElvenideCore tags:
     * <ul>
     *     <li>Custom color tags created by you with {@link #addColorTag(String, String)}</li>
     *     <li><code>&lt;egradient:{color1}:{color2...}:[phase]&gt;</code> tags that support your custom colors</li>
     *     <li><code>&lt;escape:'{text}'&gt;</code> tags that escape any MiniMessage tags in them</li>
     *     <li><code>&lt;eshadow:{color}:[opacity]&gt;</code> tags that support your custom colors</li>
     * </ul>
     * @param text The String text
     * @param optionalPlaceholders Optional placeholders
     * @return Deserialized MiniMessage component
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull Component from(@Nullable Object text, @Nullable Object... optionalPlaceholders) {
        return resolver().deserialize(
            preParsing(valueOf(text), optionalPlaceholders),
            customResolver.build()
        );
    }

    /**
     * Deserializes a String with support for placeholders provided by a third-party plugin.
     * <p>
     * Example:
     * <code>
     *     from("Hi %player_name%", player, PlaceholderAPI::setPlaceholders);
     * </code>
     * @param text The String text
     * @param player The optional player
     * @param placeholderResolver The placeholder resolver
     * @return Deserialized MiniMessage component
     * @see #from(Object, Object...)
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull Component from(@Nullable Object text, @Nullable Player player, @NotNull BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        text = placeholderResolver.apply(player, valueOf(text));
        return from(text);
    }

    /**
     * Strips all valid MiniMessage/ElvenideCore tags from a String.
     * @param text The String text
     * @return Plain text without component tags
     * @since 0.0.17
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String stripTags(@NotNull String text) {
        return resolver().stripTags(text);
    }

    /**
     * Convenience method to send a message using {@link #from(Object, Object...)} to a player, group,
     * console, or the entire server.
     * @param audience The audience (e.g. player)
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public final void send(@NotNull Audience audience, @Nullable Object text, @Nullable Object... optionalPlaceholders) {
        audience.sendMessage(from(text, optionalPlaceholders));
    }

    /**
     * Convenience method to send a message using {@link #from(Object, Object...)} to a player
     * with support for a third-party placeholder plugin.
     * @param player The player
     * @param text String text
     * @param placeholderResolver The placeholder resolver
     * @since 0.0.15
     */
    @PublicAPI
    public final void send(@NotNull Player player, @Nullable Object text, @NotNull BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        player.sendMessage(from(text, player, placeholderResolver));
    }

    /**
     * Convenience method to send a title using {@link #from(Object, Object...)} to a player, group,
     * or the entire server.
     * @param audience The audience (e.g. player)
     * @param title Title text
     * @param subtitle Subtitle text
     * @since 0.0.17
     */
    @PublicAPI
    public final void sendTitle(@NotNull Audience audience, @NotNull Object title, @NotNull Object subtitle) {
        audience.showTitle(Title.title(
            from(title),
            from(subtitle)
        ));
    }

    /**
     * Convenience method to send a title using {@link #from(Object, Object...)} to a player, group,
     * or the entire server.
     * @param audience The audience (e.g. player)
     * @param title Title text
     * @param subtitle Subtitle text
     * @param fadeInTicks Tick duration to fade title in
     * @param stayTicks Tick duration to show title
     * @param fadeOutTicks Tick duration to fade title out
     * @since 0.0.17
     */
    @PublicAPI
    public final void sendTitle(@NotNull Audience audience, @NotNull Object title, @NotNull Object subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        audience.showTitle(Title.title(
            from(title),
            from(subtitle),
            fadeInTicks,
            stayTicks,
            fadeOutTicks
        ));
    }

    /**
     * Convenience method to send an action bar using {@link #from(Object, Object...)} to a player, group,
     * or the entire server.
     * @param audience The audience (e.g. player)
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.17
     */
    @PublicAPI
    public final void sendActionBar(@NotNull Audience audience, @NotNull Object text, @Nullable Object... optionalPlaceholders) {
        audience.sendActionBar(from(text, optionalPlaceholders));
    }

    /**
     * Adds a custom color tag parseable by {@link #from(Object, Object...)}.
     * <p>
     * Example:<br/>
     * <code>addColorTag("bright_red", "#ff0000")</code><br/>
     * will add <code>&lt;bright_red&gt;</code>
     * @param name The name of the tag, in a valid MiniMessage tag name pattern
     * @param color The color of the tag
     */
    @PublicAPI
    public final void addColorTag(@NotNull @TagPattern String name, @NotNull String color) {
        customResolver.resolver(createCustomColorResolver(name, color));
    }

    /**
     * Adds a custom text tag parseable by {@link #from(Object, Object...)}.
     * <p>
     * Example:<br/>
     * <code>addTextTag("name", "John Doe")</code><br/>
     * will add <code>&lt;name&gt;</code> that evaluates to "John Doe".
     * @param name The name of the tag, in a valid MiniMessage tag name pattern
     * @param text The text value of the tag
     * @since 0.0.18
     * @apiNote
     * Text values are added as pre-parsed content, so they can contain MiniMessage tags
     * and other ElvenideCore custom tags. To avoid infinite recursion, avoid
     * referencing a text tag in its own text.
     */
    @PublicAPI
    public final void addTextTag(@NotNull @TagPattern String name, @NotNull String text) {
        customResolver.resolver(createCustomTextResolver(name, text));
    }

}
