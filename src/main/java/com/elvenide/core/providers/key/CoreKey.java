package com.elvenide.core.providers.key;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Represents a core key in ElvenideCore.
 * Core keys are used to easily define and retrieve namespaced keys.
 * <p>
 * When creating core keys via the enum method, the key values are dependent on the names of the enum members and their declaring classes.
 * @apiNote
 * There are three ways of creating core keys:
 * <ul>
 *     <li>Creating an enum that implements this interface (each enum field becomes a core key in your plugin's namespace)</li>
 *     <li>Setting a static field to the result of the {@link #of(String) CoreKey.of(String)} method (your plugin's namespace)</li>
 *     <li>Setting a static field to the result of the {@link #of(String, String) CoreKey.of(String, String)} method (custom namespace)</li>
 * </ul>
 * @since 0.0.19
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 */
@PublicAPI
public interface CoreKey {

    private static void ensureInitialized() throws IllegalStateException {
        if (!Core.plugin.isSet())
            throw new IllegalStateException("Your plugin is using ElvenideCore features that require initialization, please do so via Core.plugin.set()");
    }

    /**
     * Retrieves the core key's value, as a namespaced key.
     *
     * @return The core key's value
     * @since 0.0.19
     */
    @PublicAPI
    @Contract(pure = true)
    default @NotNull NamespacedKey get() throws IllegalStateException {
        return new NamespacedKey(
            namespace(),
            keyString()
        );
    }

    /**
     * Retrieves the core key's namespace.
     * Override this method to set a custom namespace.
     *
     * @return The core key's namespace
     * @since 0.0.19
     */
    @PublicAPI
    @Contract(pure = true)
    default @NotNull String namespace() throws IllegalStateException {
        ensureInitialized();
        return Core.plugin.get().getName().toLowerCase(Locale.ROOT);
    }

    /**
     * Retrieves the core key's key string.
     *
     * @return The core key's key string
     * @since 0.0.19
     */
    @PublicAPI
    @Contract(pure = true)
    default @NotNull String keyString() throws IllegalStateException {
        if (this instanceof Enum<?> key)
            return key.getDeclaringClass().getSimpleName().toLowerCase() + "/" + key.name().toLowerCase();
        else
            throw new IllegalStateException("CoreKeys must either be implemented on an Enum class or created using CoreKey.of().");
    }

    /**
     * Creates a new CoreKey with the given key string and your plugin's namespace.
     * @param key The key string
     * @return The new CoreKey
     * @since 0.0.19
     * @apiNote
     * CoreKeys made with this method can only be used after the plugin has been initialized using
     * {@link com.elvenide.core.providers.plugin.PluginProvider#set(JavaPlugin) Core.plugin.set(JavaPlugin)}.
     */
    @PublicAPI
    @Contract("_ -> new")
    static CoreKey of(String key) {
        return new CoreKey() {
            @Override
            public @NotNull String keyString() {
                return key;
            }
        };
    }

    /**
     * Creates a new CoreKey with the given key string and the given custom namespace.
     * @param namespace The namespace
     * @param key The key string
     * @return The new CoreKey
     * @since 0.0.19
     */
    @PublicAPI
    @Contract("_, _ -> new")
    static CoreKey of(String namespace, String key) {
        return new CoreKey() {
            @Override
            public @NotNull String namespace() {
                return namespace;
            }

            @Override
            public @NotNull String keyString() {
                return key;
            }
        };
    }

}
