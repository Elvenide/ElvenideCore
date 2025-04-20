package com.elvenide.core.providers.lang;

import com.elvenide.core.ElvenideCore;

/**
 * Represents a supplier of ElvenideCore lang keys.
 * @since 0.0.8
 */
public interface LangKeySupplier {
    /**
     * Create a lang key with placeholders.
     * @param key The key
     * @param defValue The default value
     * @param firstPlaceholder The first placeholder
     * @param otherPlaceholders Optional additional placeholders
     * @return The lang tag, with placeholders
     * @since 0.0.13
     */
    default String create(@LangPattern String key, String defValue, @LangPlaceholderPattern String firstPlaceholder, String... otherPlaceholders) {
        ElvenideCore.lang.set(key, defValue);
        String placeholders = ":" + firstPlaceholder + ":" + String.join(":", otherPlaceholders);
        if (placeholders.endsWith(":"))
            placeholders = placeholders.substring(0, placeholders.length() - 1);
        return ElvenideCore.lang.tag(key + placeholders);
    }

    /**
     * Create a lang key.
     * @param key The key
     * @param defValue The default value
     * @return The lang tag
     * @since 0.0.8
     */
    default String create(@LangPattern String key, String defValue) {
        ElvenideCore.lang.set(key, defValue);
        return ElvenideCore.lang.tag(key);
    }
}