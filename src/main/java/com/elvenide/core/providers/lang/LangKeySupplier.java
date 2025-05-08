package com.elvenide.core.providers.lang;

import com.elvenide.core.Core;

import java.util.Arrays;

/**
 * Represents a supplier of ElvenideCore lang keys.
 * @since 0.0.8
 */
public interface LangKeySupplier {
    /// Internally escapes quotes for MiniMessage
    private static String esc(String p) {
        return p.replace("\"", "\\\"");
    }

    /**
     * Create a lang key with placeholders.
     * @param key The key
     * @param defValue The default value
     * @param firstPlaceholder The first placeholder
     * @param otherPlaceholders Optional additional placeholders
     * @return The lang tag, with placeholders
     * @since 0.0.13
     */
    default LangKey create(@LangPattern String key, String defValue, @LangPlaceholderPattern String firstPlaceholder, String... otherPlaceholders) {
        Core.lang.set(key, defValue);
        String placeholders = ":\"" + esc(firstPlaceholder) + "\"" + Arrays.stream(otherPlaceholders).map(p -> ":\"" + esc(p) + "\"").reduce("", String::concat);
        return Core.lang.tag(key + placeholders);
    }

    /**
     * Create a lang key.
     * @param key The key
     * @param defValue The default value
     * @return The lang tag
     * @since 0.0.8
     */
    default LangKey create(@LangPattern String key, String defValue) {
        Core.lang.set(key, defValue);
        return Core.lang.tag(key);
    }
}