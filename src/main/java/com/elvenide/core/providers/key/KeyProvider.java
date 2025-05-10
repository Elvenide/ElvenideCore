package com.elvenide.core.providers.key;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.plugin.CorePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#keys} field.
 */
public class KeyProvider extends Provider {

    @ApiStatus.Internal
    public KeyProvider(@Nullable Core core) {
        super(core);
    }

    private String generateKeyStringFromEnum(@NotNull Enum<?> key) {
        return key.getDeclaringClass().getSimpleName().toLowerCase() + "/" + key.name().toLowerCase();
    }

    /**
     * This method is obsolete and will be removed in a future version.
     * <p>
     * As of v0.0.15, creating a plugin that extends {@link CorePlugin} automatically initializes the key provider and
     * is the recommended alternative to this method.
     * @param plugin Your plugin
     * @deprecated Use {@link CorePlugin} or {@link Core#setPlugin(JavaPlugin)} instead
     */
    @Deprecated(forRemoval = true, since = "0.0.15")
    public void init(@NotNull JavaPlugin plugin) {
        Core.setPlugin(plugin);
    }

    /**
     * Gets the NamespacedKey with the given key string.
     * @param key The key string
     * @return The NamespacedKey
     */
    @Contract(pure = true)
    public @NotNull NamespacedKey get(@NotNull String key) {
        ensureInitialized();
        return new NamespacedKey(core.plugin, key);
    }

    /**
     * Gets the NamespacedKey with a key string generated from the given enum.
     * <p>
     * Note: The generated key string is dependent on the name of the enum and its declaring class.
     * Changing the name of an enum or declaring class will produce a different key.
     * @param key The enum
     * @return The NamespacedKey
     */
    @Contract(pure = true)
    public @NotNull NamespacedKey get(@NotNull Enum<?> key) {
        return get(generateKeyStringFromEnum(key));
    }
}
