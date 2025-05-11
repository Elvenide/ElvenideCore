package com.elvenide.core.providers.lang;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/// Used internally to represent ElvenideCore lang keys.
public class LangKey {

    private final String input;

    /**
     * Creates a new LangKey representation. ONLY FOR INTERNAL USE.
     * @param input The key tag
     */
    @ApiStatus.Internal
    LangKey(String input) {
        this.input = input;
    }

    /**
     * Gets the key tag, substituting placeholders appropriately.
     * @param placeholders The placeholders to substitute
     * @return The key tag with placeholders substituted
     */
    @PublicAPI
    @Contract(pure = true)
    public String formatted(Object... placeholders) {
        return Core.text.format(input, placeholders);
    }

    /**
     * Gets the raw key tag, without substituting placeholders.
     * @return The raw key tag
     */
    @PublicAPI
    @Contract(pure = true)
    @Override
    public String toString() {
        return input;
    }
}
