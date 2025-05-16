package com.elvenide.core.providers.text;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.lang.LangProvider;
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
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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
    private static final TagResolver.Builder customColorResolver = TagResolver.builder();
    private static final HashMap<String, String> customColors = new HashMap<>();

    /// Flag that handles whether &lt;gradient&gt; tags should be auto-converted to &lt;egradient&gt;
    @PublicAPI
    public static boolean autoConvertGradientToEgradient = true;

    /// Flag that handles whether &lt;shadow&gt; tags should be auto-converted to &lt;eshadow&gt;
    @PublicAPI
    public static boolean autoConvertShadowToEshadow = true;

    /// A set of built-in text packages that add additional custom tags to MiniMessage
    @PublicAPI
    public final CommonTextPackages packages = new CommonTextPackages();

    @ApiStatus.Internal
    public TextProvider(@Nullable Core core) {
        super(core);
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
                        .tag("elang", TextProvider::createElangTag)
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
                .replace("</gradient>", "</egradient>");

        // Auto-convert <shadow> to <eshadow>
        if (autoConvertShadowToEshadow)
            text = text.replace("<shadow:", "<eshadow:")
                .replace("</shadow>", "</eshadow>");

        // Format placeholders
        if (placeholders.length > 0)
            text = Core.text.format(text, placeholders);

        // Convert legacy colors
        return convertLegacyToMiniMessage(text);
    }

    /// @since 0.0.2
    private static TagResolver createCustomColorResolver(@TagPattern String name, String color) {
        if (customColors.containsKey(name))
            throw new IllegalArgumentException("Tag name already in use: " + name);

        customColors.put(name, color);
        return TagResolver.resolver(name, Tag.styling(
                Objects.requireNonNull(TextColor.fromHexString(color))
        ));
    }

    /// @since 0.0.3
    private static Tag createElangTag(final ArgumentQueue args, final Context ignored) {
        final @Subst("key") String key = args.popOr("The <elang> tag requires at least one argument, the key to replace with a lang value.").value();
        final String value = Core.lang.get(key);

        ArrayList<String> placeholders = new ArrayList<>();
        while (args.hasNext()) {
            placeholders.add(args.pop().value());
        }

        return Tag.preProcessParsed(TextProvider.preParsing(value, placeholders.toArray(String[]::new)));
    }

    /// @since 0.0.11
    private static Tag createEgradientTag(final ArgumentQueue args, final Context ignored) {
        final String first = args.popOr("The <egradient> tag requires at least two color arguments; none were provided.").value();
        final String second = args.popOr("The <egradient> tag requires at least two color arguments; only one was provided.").value();

        StringBuilder value = new StringBuilder("<gradient:")
            .append(customColors.getOrDefault(first, first)).append(":")
            .append(customColors.getOrDefault(second, second));

        while (args.hasNext()) {
            String arg = args.pop().value();
            value.append(":").append(customColors.getOrDefault(arg, arg));
        }
        value.append(">");

        return Tag.preProcessParsed(value.toString());
    }

    /// @since 0.0.12
    private static Tag createEscapeTag(final ArgumentQueue args, final Context ignored) {
        StringBuilder value = new StringBuilder(args.popOr("The <escaped> tag requires exactly one argument, the text to escape.").value());
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
            .append(customColors.getOrDefault(color, color));

        if (args.hasNext()) {
            String opacity = args.pop().value();
            value.append(":").append(opacity);
        }
        value.append(">");

        return Tag.preProcessParsed(value.toString());
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
    public final @NotNull String format(Object rawText, Object... placeholders) {
        String text = String.valueOf(rawText);

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
                placeholders[i] = String.valueOf(placeholders[i]);

            output[matcher.start()] = '%';
            output[matcher.start() + 1] = formatLetter;
            i++;
        }

        // Format text using java placeholder system
        return String.valueOf(output).formatted(placeholders);
    }

    /**
     * Serializes a MiniMessage Component to a String in MiniMessage format.
     * Automatically escapes any tags present within the component.
     * <p>
     * Supports custom ElvenideCore tags:
     * <ul>
     *     <li><code>&lt;elang:{key}&gt;</code> tags provided by the {@link LangProvider}</li>
     *     <li>Custom color tags created by you with {@link #addColorTag(String, String)}</li>
     *     <li><code>&lt;egradient:{color1}:{color2...}:[phase]&gt;</code> tags that support your custom colors</li>
     *     <li><code>&lt;escape:'{text}'&gt;</code> tags that escape any MiniMessage tags in them</li>
     *     <li><code>&lt;eshadow:{color}:[opacity]&gt;</code> tags that support your custom colors</li>
     * </ul>
     * @param component The component
     * @return Serialized text
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String serialize(Component component) {
        return resolver().serialize(component);
    }

    /**
     * Serializes a MiniMessage Component to a String in MiniMessage format.
     * Does not escape any tags present within the component.
     * @param component The component
     * @return Serialized text
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull String serializeWithoutEscaping(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Deserializes a String in MiniMessage format to a MiniMessage Component.
     * <p>
     * Supports custom ElvenideCore tags:
     * <ul>
     *     <li><code>&lt;elang:{key}&gt;</code> tags provided by the {@link LangProvider}</li>
     *     <li>Custom color tags created by you with {@link #addColorTag(String, String)}</li>
     *     <li><code>&lt;egradient:{color1}:{color2...}:[phase]&gt;</code> tags that support your custom colors</li>
     *     <li><code>&lt;escape:'{text}'&gt;</code> tags that escape any MiniMessage tags in them</li>
     *     <li><code>&lt;eshadow:{color}:[opacity]&gt;</code> tags that support your custom colors</li>
     * </ul>
     * @param text The String text
     * @param optionalPlaceholders Optional placeholders
     * @return Deserialized MiniMessage component
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull Component deserialize(Object text, Object... optionalPlaceholders) {
        return resolver().deserialize(
                preParsing(String.valueOf(text), optionalPlaceholders),
                customColorResolver.build()
        );
    }

    /**
     * Deserializes a String with support for placeholders provided by a third-party plugin.
     * <p>
     * Example:
     * <code>
     *     deserialize("Hi %player_name%", player, PlaceholderAPI::setPlaceholders);
     * </code>
     * @param text The String text
     * @param player The optional player
     * @param placeholderResolver The placeholder resolver
     * @return Deserialized MiniMessage component
     * @see #deserialize(Object, Object...)
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public final @NotNull Component deserialize(Object text, @Nullable Player player, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        text = placeholderResolver.apply(player, String.valueOf(text));
        return deserialize(text);
    }

    /**
     * Convenience method to send a message using {@link #deserialize(Object, Object...)} to a player, group,
     * console, or the entire server.
     * @param audience The audience (e.g. player)
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public final void send(Audience audience, Object text, Object... optionalPlaceholders) {
        audience.sendMessage(deserialize(text, optionalPlaceholders));
    }

    /**
     * Convenience method to send a message using {@link #deserialize(Object, Object...)} to a player
     * with support for a third-party placeholder plugin.
     * @param player The player
     * @param text String text
     * @param placeholderResolver The placeholder resolver
     * @since 0.0.15
     */
    @PublicAPI
    public final void send(Player player, Object text, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        player.sendMessage(deserialize(text, player, placeholderResolver));
    }

    /**
     * Adds a custom color tag parseable by {@link #deserialize(Object, Object...)}.
     * <p>
     * For example:<br/>
     * <code>addColorTag("bright_red", "#ff0000")</code> will add
     * <code>&lt;bright_red&gt;</code>
     * @param name The name of the tag, in a valid MiniMessage tag name pattern
     * @param color The color of the tag
     */
    @PublicAPI
    public final void addColorTag(@TagPattern String name, String color) {
        customColorResolver.resolver(createCustomColorResolver(name, color));
    }

    public static class CommonTextPackages implements TextPackageSupplier {
        private CommonTextPackages() {}

        /**
         * Introduces brighter variants of existing colors, including:
         * <ul>
         *     <li><code>&lt;bright_red&gt;</code> (red)</li>
         *     <li><code>&lt;bright_blue&gt;</code> (blue)</li>
         *     <li><code>&lt;bright_pink&gt;</code> (light_purple)</li>
         *     <li><code>&lt;bright_yellow&gt;</code> (yellow)</li>
         * </ul>
         * @since 0.0.10
         */
        @PublicAPI
        public final TextPackageSupplier brightColors = new BrightColorsPackage();

        /**
         * Introduces aliases for existing colors, including:
         * <ul>
         *     <li><code>&lt;pink&gt;</code> (light_purple)</li>
         *     <li><code>&lt;purple&gt;</code> (dark_purple)</li>
         *     <li><code>&lt;cyan&gt;</code> (dark_aqua)</li>
         *     <li><code>&lt;light_blue&gt;</code> (blue)</li>
         * </ul>
         * @since 0.0.10
         */
        @PublicAPI
        public final TextPackageSupplier colorAliases = new ColorAliasesPackage();

        /**
         * Introduces a set of additional MiniMessage colors, including:
         * <ul>
         *     <li><code>&lt;brown&gt;</code></li>
         *     <li><code>&lt;orange&gt;</code></li>
         *     <li><code>&lt;orange_red&gt;</code></li>
         *     <li><code>&lt;smooth_purple&gt;</code></li>
         *     <li><code>&lt;indigo&gt;</code></li>
         *     <li><code>&lt;smooth_blue&gt;</code></li>
         * </ul>
         * @since 0.0.10
         */
        @PublicAPI
        public final TextPackageSupplier moreColors = new MoreColorsPackage();

        @ApiStatus.Internal
        @Override
        public void build(TextProvider textProvider) {
            brightColors.build(textProvider);
            colorAliases.build(textProvider);
            moreColors.build(textProvider);
        }

        /**
         * Installs all built-in packages, including:
         * <ul>
         *     <li>{@link #brightColors Bright Colors}</li>
         *     <li>{@link #colorAliases Color Aliases}</li>
         *     <li>{@link #moreColors More Colors}</li>
         * </ul>
         */
        @PublicAPI
        @Override
        public void install() {
            build(Core.text);
        }

        /**
         * Installs a provided third-party package.
         * @param textPackage The package
         */
        @PublicAPI
        public void installExternal(TextPackageSupplier textPackage) {
            textPackage.build(Core.text);
        }
    }

}
