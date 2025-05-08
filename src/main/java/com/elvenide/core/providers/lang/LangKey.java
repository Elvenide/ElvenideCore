package com.elvenide.core.providers.lang;

import com.elvenide.core.Core;
import org.jetbrains.annotations.ApiStatus;

/// Used internally to represent ElvenideCore lang keys.
public class LangKey {

    private final String input;

    /**
     * Creates a new LangKey representation. ONLY FOR INTERNAL USE.
     * @param input The key tag
     */
    @ApiStatus.Internal
    public LangKey(String input) {
        this.input = input;
    }

    /**
     * Gets the key tag, substituting placeholders appropriately.
     * @param placeholders The placeholders to substitute
     * @return The key tag with placeholders substituted
     */
    public String formatted(Object... placeholders) {
        return Core.text.format(input, placeholders);
    }

    /**
     * Gets the raw key tag, without substituting placeholders.
     * @return The raw key tag
     */
    @Override
    public String toString() {
        return input;
    }
}
