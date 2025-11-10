package com.elvenide.core.providers.lang;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

/**
 * Represents a lang key in ElvenideCore.
 * Lang keys are used to store and retrieve centralized, configurable messages for your plugin.
 * @apiNote
 * There are two ways of creating lang keys:
 * <ul>
 *     <li>Creating an enum that implements this interface (each enum field becomes a lang key)</li>
 *     <li>Setting a static field to the result of the {@link #of(String) LangKey.of(String)} method</li>
 * </ul>
 * @since 0.0.17
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 */
public interface LangKey {
    /**
     * For INTERNAL USE ONLY.<br/>
     * Do not use this field, this implementation is subject to change without warning.
     */
    @ApiStatus.Internal
    HashMap<LangKey, Object> LANG_KEY_VALUE_MAP_0X3693 = new HashMap<>();

    /**
     * Retrieves the lang key's value, formatted with the given optional placeholders.
     *
     * @param optionalPlaceholders Optional placeholders
     * @return The lang key's value
     * @since 0.0.17
     */
    default String get(Object... optionalPlaceholders) {
        return Core.text.format(LANG_KEY_VALUE_MAP_0X3693.get(this), optionalPlaceholders);
    }

    /**
     * Sets the lang key's value.
     * @param value The value
     * @since 0.0.17
     */
    default void set(String value) {
        LANG_KEY_VALUE_MAP_0X3693.put(this, value);
    }

    /**
     * Creates a new LangKey with the given value.
     * @param value The value
     * @return The new LangKey
     * @since 0.0.17
     */
    @PublicAPI
    static LangKey of(String value) {
        LangKey key = new LangKey() {};
        key.set(value);
        return key;
    }

}
