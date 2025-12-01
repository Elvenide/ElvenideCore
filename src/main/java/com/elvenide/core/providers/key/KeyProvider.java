package com.elvenide.core.providers.key;

import com.destroystokyo.paper.entity.ai.GoalKey;
import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class should not be directly referenced by any plugin.
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
     * @deprecated Use {@link CoreKey#get()} instead.
     * @param key The key string
     * @return The NamespacedKey
     */
    @Deprecated(since = "0.0.19", forRemoval = true)
    @Contract(pure = true)
    public @NotNull NamespacedKey get(@NotNull String key) {
        ensureInitialized();
        return new NamespacedKey(Core.plugin.get(), key);
    }

    /**
     * @deprecated Use {@link CoreKey#get()} instead.
     * @param key The enum
     * @return The NamespacedKey
     */
    @Deprecated(since = "0.0.19", forRemoval = true)
    @Contract(pure = true)
    public @NotNull NamespacedKey get(@NotNull Enum<?> key) {
        return get(generateKeyStringFromEnum(key));
    }

    /**
     * This method is deprecated as it is unnecessary, since it is just a direct alias of the NamespacedKey constructor.
     * @deprecated Use {@link CoreKey#get()} instead.
     * @param namespace The namespace
     * @param key The key string
     * @return The NamespacedKey
     */
    @Deprecated(since = "0.0.19", forRemoval = true)
    @Contract(pure = true)
    public @NotNull NamespacedKey get(@NotNull String namespace, @NotNull String key) {
        ensureInitialized();
        return new NamespacedKey(namespace, key);
    }

    /**
     * @deprecated Use {@link CoreKey#get()} instead.
     * @param namespace The namespace
     * @param key The enum
     * @return The NamespacedKey
     */
    @Deprecated(since = "0.0.19", forRemoval = true)
    @Contract(pure = true)
    public @NotNull NamespacedKey get(@NotNull String namespace, @NotNull Enum<?> key) {
        return get(namespace, generateKeyStringFromEnum(key));
    }

    /**
     * This method is obsolete.
     * @deprecated Use {@link CoreKey#get()} as the NamespacedKey parameter of {@link GoalKey#of(Class, NamespacedKey)} instead.
     * @param key String key
     * @param type Mob type
     * @return GoalKey
     * @since 0.0.17
     */
    @Deprecated(since = "0.0.19", forRemoval = true)
    @Contract(pure = true)
    public <T extends Mob> @NotNull GoalKey<T> getGoal(@NotNull String key, @NotNull Class<T> type) {
        return GoalKey.of(type, get(key));
    }

    /**
     * This method is obsolete.
     * @deprecated Use {@link CoreKey#get()} as the NamespacedKey parameter of {@link GoalKey#of(Class, NamespacedKey)} instead.
     * @param key Enum key
     * @param type Mob type
     * @return GoalKey
     * @since 0.0.17
     */
    @Deprecated(since = "0.0.19", forRemoval = true)
    @Contract(pure = true)
    public <T extends Mob> @NotNull GoalKey<T> getGoal(@NotNull Enum<?> key, @NotNull Class<T> type) {
        return GoalKey.of(type, get(key));
    }
}
