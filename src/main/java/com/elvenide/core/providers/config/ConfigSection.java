package com.elvenide.core.providers.config;

import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.text.TextProvider;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

/**
 * Represents a section of an ElvenideCore config file.
 * @since 0.0.15
 */
@PublicAPI
public interface ConfigSection extends ConfigurationSection {

    private void assertExists(@NotNull String key) {
        if (!contains(key))
            throw new IllegalArgumentException("Key %s does not exist in this config.".formatted(key));
    }

    private @NotNull String getExistentString(@NotNull String key) {
        return getString(key, "");
    }

    /**
     * Convenience alias for {@link #getKeys(boolean) getKeys(false)}.
     * @return Set of keys directly applied to this config section
     * @since 0.0.15
     */
    @PublicAPI
    default @NotNull Set<String> getKeys() {
        return getKeys(false);
    }

    /**
     * Convenience alias for {@link #getValues(boolean) getValues(false)}.
     * @return Map of values directly applied to this config section
     * @since 0.0.15
     */
    @PublicAPI
    default @NotNull Map<String, Object> getValues() {
        return getValues(false);
    }

    /**
     * Gets a NamespacedKey from the config.
     * @param key String key
     * @return NamespacedKey
     * @throws IllegalArgumentException If key does not exist or value is invalid namespaced key
     * @since 25.1
     */
    @PublicAPI
    default @NotNull NamespacedKey getNamespacedKey(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        String input = getExistentString(key)
            .toLowerCase()
            .replace(" ", "_")
            .replace("-", "_");
        NamespacedKey output = NamespacedKey.fromString(input);
        if (output == null)
            throw new IllegalArgumentException("Invalid namespaced key: " + getExistentString(key));
        return output;
    }

    /**
     * Gets a URI from the config.
     * @param key String key
     * @return URI
     * @since 0.0.2
     * @throws IllegalArgumentException If key does not exist or value is invalid URI
     */
    @PublicAPI
    default @NotNull URI getURI(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        String value = getExistentString(key);

        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Failed to parse URI: " + value, e);
        }
    }

    /**
     * Gets a Sound from the config.
     * @param key String key
     * @return Sound
     * @since 0.0.15
     * @throws IllegalArgumentException If key does not exist or value is invalid sound
     */
    @PublicAPI
    default @NotNull Sound getSound(@NotNull String key) throws IllegalArgumentException {
        Sound output = Registry.SOUND_EVENT.get(getNamespacedKey(key));
        if (output == null)
            throw new IllegalArgumentException("Failed to parse Sound: " + getExistentString(key));
        return output;
    }

    /**
     * Gets a PotionEffectType from the config.
     * @param key String key
     * @return PotionEffectType
     * @since 0.0.15
     * @throws IllegalArgumentException If key does not exist or value is invalid potion effect type
     */
    @PublicAPI
    default @NotNull PotionEffectType getPotionEffectType(@NotNull String key) throws IllegalArgumentException {
        PotionEffectType type = Registry.MOB_EFFECT.get(getNamespacedKey(key));
        if (type == null)
            throw new IllegalArgumentException("Failed to parse PotionEffectType: " + getExistentString(key));
        return type;
    }

    /**
     * Gets a Material from the config.
     * @param key String key
     * @return Material
     * @since 0.0.15
     * @throws IllegalArgumentException If key does not exist or value is invalid material
     */
    @PublicAPI
    default @NotNull Material getMaterial(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        String value = getExistentString(key);

        Material material = Material.matchMaterial(value);
        if (material == null)
            throw new IllegalArgumentException("Failed to parse Material: " + value);

        return material;
    }

    /**
     * Gets a Material from the config.
     * Supports legacy material names, which will return the Material that represents their modern equivalent.
     * @param key String key
     * @return Material
     * @since 0.0.15
     * @deprecated
     * Legacy material names are barely supported by Paper and should not be used.
     * Use {@link #getMaterial(String)} with modern material names instead.
     */
    @Deprecated(since = "25.1", forRemoval = true)
    default @NotNull Material getMaterialLegacy(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        Material currentMaterial = Material.matchMaterial(value);
        Material material = Material.matchMaterial(value, true);

        if (currentMaterial == null && material == null)
            throw new IllegalArgumentException("Failed to parse Material: " + value);

        if (material == null)
            return currentMaterial;

        return material;
    }

    /**
     * Gets an EntityType from the config.
     * @param key String key
     * @return EntityType
     * @since 0.0.15
     * @throws IllegalArgumentException If key does not exist or value is invalid entity type
     */
    @PublicAPI
    default @NotNull EntityType getEntityType(@NotNull String key) throws IllegalArgumentException {
        EntityType type = Registry.ENTITY_TYPE.get(getNamespacedKey(key));
        if (type == null)
            throw new IllegalArgumentException("Failed to parse EntityType: " + getExistentString(key));
        return type;
    }

    /**
     * Gets a Particle from the config.
     * @param key String key
     * @return Particle
     * @since 0.0.15
     * @throws IllegalArgumentException If key does not exist or value is invalid particle type
     */
    @PublicAPI
    default @NotNull Particle getParticle(@NotNull String key) throws IllegalArgumentException {
        Particle particle = RegistryAccess.registryAccess().getRegistry(RegistryKey.PARTICLE_TYPE).get(getNamespacedKey(key));
        if (particle == null)
            throw new IllegalArgumentException("Failed to parse Particle: " + getExistentString(key));
        return particle;
    }

    /**
     * Gets a Color from hex/rgb format in the config.
     * @param key String key
     * @return Color
     * @since 0.0.15
     * @throws IllegalArgumentException If key does not exist or value is invalid color format
     */
    @PublicAPI
    default @NotNull Color getColorFromString(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        String value = getExistentString(key);

        try {
            if (value.startsWith("#")) {
                // Hex format
                return Color.fromRGB(
                    Integer.valueOf(value.substring(1, 3), 16),
                    Integer.valueOf(value.substring(3, 5), 16),
                    Integer.valueOf(value.substring(5, 7), 16));
            } else {
                // RGB format
                String[] rgb = value.split(",");
                return Color.fromRGB(
                    Integer.parseInt(rgb[0].strip()),
                    Integer.parseInt(rgb[1].strip()),
                    Integer.parseInt(rgb[2].strip()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Color: '%s'; not in Hex (#rrggbb) or RGB (r,g,b) format.".formatted(value));
        }
    }

    /**
     * Gets a float from the config.
     * @param key String key
     * @return Float
     * @since 0.0.15
     */
    @PublicAPI
    default float getFloat(@NotNull String key) {
        return Double.valueOf(getDouble(key)).floatValue();
    }

    /**
     * Gets a float from the config.
     * @param key String key
     * @return Float
     * @since 0.0.15
     */
    @PublicAPI
    default float getFloat(@NotNull String key, float defaultValue) {
        return Double.valueOf(getDouble(key, defaultValue)).floatValue();
    }

    /**
     * Gets a Component from a MiniMessage-formatted String in the config.
     * Supports custom tags/colors added by ElvenideCore.
     * @param path String key
     * @return Component
     * @since 0.0.15
     */
    @PublicAPI
    @Override
    default @Nullable Component getRichMessage(@NotNull String path) {
        return getRichMessage(path, null);
    }

    /**
     * Gets a Component from a MiniMessage-formatted String in the config, or a fallback.
     * Supports custom tags/colors added by ElvenideCore.
     * @param path String key
     * @param fallback Component fallback
     * @return Component
     * @since 0.0.15
     */
    @PublicAPI
    @Override
    @Nullable
    default Component getRichMessage(@NotNull String path, @Nullable Component fallback) {
        return getComponent(path, TextProvider.resolver(), fallback);
    }

    /**
     * Sets a Component as a MiniMessage-formatted String in the config.
     * Supports custom tags/colors added by ElvenideCore.
     * @param path String key
     * @param value Component value
     * @since 0.0.15
     */
    @PublicAPI
    @Override
    default void setRichMessage(@NotNull String path, @Nullable Component value) {
        setComponent(path, TextProvider.resolver(), value);
    }

    /**
     * Sets a value and immediately saves the config.
     * Best used when you only need to set a single value before saving.
     * @param key String key
     * @param value Value
     * @since 0.0.15
     */
    @PublicAPI
    default void setAndSave(@NotNull String key, @Nullable Object value) {
        set(key, value);
        getRoot().save();
    }

    /**
     * Removes a value from the config.
     * Simply a clearer alias for <code>set(key, null)</code>.
     * @param key String key
     * @since 0.0.15
     */
    @PublicAPI
    default void remove(@NotNull String key) {
        set(key, null);
    }

    /**
     * Removes a value and immediately saves the config.
     * Best used when you only need to remove a single value before saving.
     * @param key String key
     * @since 0.0.15
     */
    @PublicAPI
    default void removeAndSave(@NotNull String key) {
        remove(key);
        getRoot().save();
    }

    /**
     * Gets the root Config that this section is from.
     * @return Config
     */
    @PublicAPI
    @Override
    @NotNull Config getRoot();

    /**
     * Gets the parent ConfigSection or Config of this section.
     * If this section is a Config, this will return null.
     * @return ConfigSection
     */
    @PublicAPI
    @Override
    @Nullable ConfigSection getParent();

    /**
     * Creates and adds a ConfigSection to the config.
     * @param path Path to the ConfigSection
     * @return ConfigurationSection
     */
    @PublicAPI
    @Override
    @NotNull ConfigSection createSection(@NotNull String path);

    /**
     * Creates and adds a ConfigSection to the config, with the given values.
     * @param path Path to the ConfigSection
     * @param map Map of keys and values to set
     * @return ConfigurationSection
     */
    @PublicAPI
    @Override
    @NotNull ConfigSection createSection(@NotNull String path, @NotNull Map<?, ?> map);

    /**
     * Gets a ConfigSection from the config.
     * <p>
     * It is recommended to use {@link #getSection(String)} instead, for consistency and reduced verbosity.
     * @param path Path to the ConfigSection
     * @return ConfigSection, or <code>null</code> if not found
     */
    @PublicAPI
    @ApiStatus.Experimental
    @Override
    @Nullable ConfigSection getConfigurationSection(@NotNull String path);

    /**
     * Gets a ConfigSection from the config.
     * <p>
     * Shorter alias for {@link #getConfigurationSection(String)}.
     * @param path Path to the ConfigSection
     * @return ConfigurationSection, or <code>null</code> if not found
     */
    @PublicAPI
    default @Nullable ConfigSection getSection(@NotNull String path) {
        return getConfigurationSection(path);
    }

    /**
     * Gets a String from the config, or throws if not found.
     * @param key String key
     * @return String
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull String getStringOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        return getString(key, "UNREACHABLE");
    }

    /**
     * Gets an Integer from the config, or throws if not found.
     * @param key String key
     * @return Integer
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull Integer getIntOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        return getInt(key, Integer.MAX_VALUE);
    }

    /**
     * Gets a Double from the config, or throws if not found.
     * @param key String key
     * @return Double
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull Double getDoubleOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        return getDouble(key, Double.MAX_VALUE);
    }

    /**
     * Gets a Float from the config, or throws if not found.
     * @param key String key
     * @return Float
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull Float getFloatOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        return getFloat(key, Float.MAX_VALUE);
    }

    /**
     * Gets a Boolean from the config, or throws if not found.
     * @param key String key
     * @return Boolean
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull Boolean getBooleanOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        return getBoolean(key, false);
    }

    /**
     * Gets a Long from the config, or throws if not found.
     * @param key String key
     * @return Long
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull Long getLongOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        return getLong(key, Long.MAX_VALUE);
    }

    /**
     * Gets a Component from a MiniMessage-formatted String in the config, or throws if not found.
     * Supports custom tags/colors added by ElvenideCore.
     * @param key String key
     * @return Component
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull Component getRichMessageOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        Component output = getRichMessage(key, null);
        assert output != null;
        return output;
    }

    /**
     * Gets a ConfigSection from the config, or throws if not found.
     * @param key String key
     * @return ConfigSection
     * @throws IllegalArgumentException If key does not exist
     * @since 25.1
     */
    @PublicAPI
    default @NotNull ConfigSection getSectionOrThrow(@NotNull String key) throws IllegalArgumentException {
        assertExists(key);
        ConfigSection output = getSection(key);
        assert output != null;
        return output;
    }
}
