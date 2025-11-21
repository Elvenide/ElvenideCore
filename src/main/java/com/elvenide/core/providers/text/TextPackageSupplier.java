package com.elvenide.core.providers.text;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.map.CoreMap;
import org.jetbrains.annotations.NotNull;

/**
 * When installed, supplies a bundle of custom ElvenideCore tags to {@link Core#text}.
 */
@PublicAPI
public interface TextPackageSupplier {

    /**
     * Used internally to get a set of custom color MiniMessage tags to register in ElvenideCore.
     * @return Map of tag names (without < and >) to their corresponding hex colors
     */
    @NotNull CoreMap<String, String> getColorTags();

    /**
     * Used internally to get a set of custom text MiniMessage tags to register in ElvenideCore.
     * @return Map of tag names (without < and >) to their corresponding text values
     */
    @NotNull CoreMap<String, String> getTextTags();

}
