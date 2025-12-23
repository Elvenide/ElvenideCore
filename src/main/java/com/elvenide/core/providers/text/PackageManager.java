package com.elvenide.core.providers.text;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.ApiStatus;

/**
 * This class should not directly be referenced by your code.
 * Use {@link TextProvider#packages Core.text.packages} instead.
 */
public class PackageManager {

    @ApiStatus.Internal
    PackageManager() {}

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

    /**
     * Installs all built-in packages, including:
     * <ul>
     *     <li>{@link #brightColors Bright Colors}</li>
     *     <li>{@link #colorAliases Color Aliases}</li>
     *     <li>{@link #moreColors More Colors}</li>
     * </ul>
     */
    @PublicAPI
    public void install() {
        install(brightColors, colorAliases, moreColors);
    }

    /**
     * Installs the provided text packages.
     * @param textPackages The packages
     */
    @PublicAPI
    public void install(TextPackageSupplier... textPackages) {
        for (TextPackageSupplier textPackage : textPackages) {
            textPackage.getColorTags().forEach(Core.text::addColorTag);
            textPackage.getTextTags().forEach(Core.text::addTextTag);
        }
    }
}
