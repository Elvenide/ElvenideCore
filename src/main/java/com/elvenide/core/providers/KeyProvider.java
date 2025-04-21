package com.elvenide.core.providers;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class KeyProvider extends Provider {

    private JavaPlugin plugin = null;
    private final HashMap<String, NamespacedKey> keys = new HashMap<>();

    public KeyProvider(@Nullable ElvenideCore core) {
        super(core);
    }

    private void ensureInitialized() throws IllegalStateException {
        if (plugin == null)
            throw new IllegalStateException("ElvenideCore.keys has not been initialized.");
    }

    private String generateKeyStringFromEnum(@NotNull Enum<?> key) {
        return key.getDeclaringClass().getSimpleName().toLowerCase() + "/" + key.name().toLowerCase();
    }

    /**
     * Initializes the NamespacedKey provider with your plugin.
     * Should be used in your plugin's <code>onLoad</code> or <code>onEnable</code> method.
     * @param plugin Your plugin
     */
    public void init(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a NamespacedKey with the given key string.
     * @param key The key
     * @since 0.0.14
     */
    public void add(@NotNull String key) {
        ensureInitialized();
        keys.put(key, new NamespacedKey(plugin, key));
    }

    /**
     * Adds a NamespacedKey with a key string generated from the given enum.
     * <p>
     * Note: the generated key string is dependent on the name of the enum and its declaring class.
     * Changing the name of an enum or declaring class will produce a different key.
     * @param key The enum
     * @since 0.0.14
     */
    public void add(@NotNull Enum<?> key) {
        add(generateKeyStringFromEnum(key));
    }

    /**
     * Gets the NamespacedKey with the given key string.
     * @param key The key string
     * @return The NamespacedKey
     */
    public @NotNull NamespacedKey get(@NotNull String key) {
        ensureInitialized();
        if (!keys.containsKey(key))
            throw new IllegalArgumentException("Unknown key: " + key);

        return keys.get(key);
    }

    /**
     * Gets the NamespacedKey with a key string generated from the given enum.
     * @param key The enum
     * @return The NamespacedKey
     */
    public @NotNull NamespacedKey get(@NotNull Enum<?> key) {
        return get(generateKeyStringFromEnum(key));
    }
}
