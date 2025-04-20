package com.elvenide.core.providers;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.text.BrightColorsPackage;
import com.elvenide.core.providers.text.ColorAliasesPackage;
import com.elvenide.core.providers.text.MoreColorsPackage;
import com.elvenide.core.providers.text.TextPackageSupplier;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class TextProvider extends Provider {
    private static MiniMessage miniMessage;
    private static final TagResolver.Builder customColorResolver = TagResolver.builder();
    private static final HashMap<String, String> customColors = new HashMap<>();

    /// Flag that handles whether &lt;gradient&gt; tags should be auto-converted to &lt;egradient&gt;
    public static boolean autoConvertGradientToEgradient = true;

    /// A set of built-in text packages that add additional custom tags to MiniMessage
    public final CommonTextPackages packages = new CommonTextPackages();

    @ApiStatus.Internal
    public TextProvider(@Nullable ElvenideCore core) {
        super(core);
    }

    private static MiniMessage mm() {
        if (miniMessage != null)
            return miniMessage;

        miniMessage = MiniMessage.builder()
                .editTags(builder ->
                    builder.resolver(StandardTags.defaults())
                        .tag("elang", LangProvider::createElangTag)
                        .tag("egradient", TextProvider::createEgradientTag)
                        .tag("escape", TextProvider::createEscapeTag)
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
    private static String preParsing(String text, Object[] placeholders) {
        if (autoConvertGradientToEgradient)
            text = text.replaceAll("<gradient:", "<egradient:");

        if (text.contains("%"))
            text = text.formatted(placeholders);

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

    /**
     * Serializes a MiniMessage Component to a String in MiniMessage format.
     * <p>
     * Supports custom ElvenideCore tags:
     * <ul>
     *     <li><code>&lt;elang:{key}&gt;</code> tags provided by the {@link LangProvider}</li>
     *     <li>Custom color tags created by you with {@link #addColorTag(String, String)}</li>
     *     <li><code>&lt;egradient:{color1}:{color2...}:[phase]&gt;</code> tags that support your custom colors</li>
     *     <li><code>&lt;escape:'{text}'&gt;</code> tags that escape any MiniMessage tags in them</li>
     * </ul>
     * @param component The component
     * @return Serialized text
     */
    public final @NotNull String serialize(Component component) {
        return mm().serialize(component);
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
     * </ul>
     * @param text The String text
     * @param optionalPlaceholders Optional placeholders
     * @return Deserialized MiniMessage component
     */
    public final @NotNull Component deserialize(String text, Object... optionalPlaceholders) {
        return mm().deserialize(
                preParsing(text, optionalPlaceholders),
                customColorResolver.build()
        );
    }

    /**
     * Adds a custom color tag parseable by {@link #deserialize(String, Object...)}.
     * <p>
     * For example:<br/>
     * <code>addColorTag("bright_red", "#ff0000")</code> will add
     * <code>&lt;bright_red&gt;</code>
     * @param name The name of the tag, in a valid MiniMessage tag name pattern
     * @param color The color of the tag
     */
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
        public final BrightColorsPackage brightColors = new BrightColorsPackage();

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
        public final ColorAliasesPackage colorAliases = new ColorAliasesPackage();

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
        public final MoreColorsPackage moreColors = new MoreColorsPackage();

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
        @Override
        public void install() {
            TextPackageSupplier.super.install();
        }

        /**
         * Installs a provided third-party package.
         * @param textPackage The package
         */
        public void installExternal(TextPackageSupplier textPackage) {
            textPackage.build(ElvenideCore.text);
        }
    }

}
