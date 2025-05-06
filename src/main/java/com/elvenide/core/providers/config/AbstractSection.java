package com.elvenide.core.providers.config;

import com.elvenide.core.providers.TextProvider;
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

public interface AbstractSection extends ConfigurationSection {

    private static NamespacedKey asMinecraftKey(String input) {
        return NamespacedKey.minecraft(input.toLowerCase().replace(" ", "_").replace("-", "_"));
    }

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
    default @NotNull Set<String> getKeys() {
        return getKeys(false);
    }

    /**
     * Convenience alias for {@link #getValues(boolean) getValues(false)}.
     * @return Map of values directly applied to this config section
     * @since 0.0.15
     */
    default @NotNull Map<String, Object> getValues() {
        return getValues(false);
    }

    /**
     * Gets a URI from the config.
     * @param key String key
     * @return URI
     * @since 0.0.2
     */
    default @NotNull URI getURI(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse URI: " + value, e);
        }
    }

    /**
     * Gets a Sound from the config.
     * @param key String key
     * @return Sound
     * @since 0.0.15
     */
    default @NotNull Sound getSound(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        Sound output = Registry.SOUND_EVENT.get(asMinecraftKey(value));
        if (output == null)
            throw new RuntimeException("Failed to parse Sound: " + value);

        return output;
    }

    /**
     * Gets a PotionEffectType from the config.
     * @param key String key
     * @return PotionEffectType
     * @since 0.0.15
     */
    default @NotNull PotionEffectType getPotionEffectType(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        PotionEffectType type = Registry.MOB_EFFECT.get(asMinecraftKey(key));
        if (type == null)
            throw new RuntimeException("Failed to parse PotionEffectType: " + value);

        return type;
    }

    /**
     * Gets a Material from the config.
     * @param key String key
     * @return Material
     * @since 0.0.15
     */
    default @NotNull Material getMaterial(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        Material material = Material.matchMaterial(value);
        if (material == null)
            throw new RuntimeException("Failed to parse Material: " + value);

        return material;
    }

    /**
     * Gets a Material from the config.
     * Supports legacy material names, which will return the Material that represents their modern equivalent.
     * @param key String key
     * @return Material
     * @since 0.0.15
     */
    default @NotNull Material getMaterialLegacy(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        Material currentMaterial = Material.matchMaterial(value);
        Material material = Material.matchMaterial(value, true);

        if (currentMaterial == null && material == null)
            throw new RuntimeException("Failed to parse Material: " + value);

        if (material == null)
            return currentMaterial;

        return material;
    }

    /**
     * Gets an EntityType from the config.
     * @param key String key
     * @return EntityType
     * @since 0.0.15
     */
    default @NotNull EntityType getEntityType(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        EntityType type = Registry.ENTITY_TYPE.get(asMinecraftKey(key));
        if (type == null)
            throw new RuntimeException("Failed to parse EntityType: " + value);

        return type;
    }

    /**
     * Gets a Particle from the config.
     * @param key String key
     * @return Particle
     * @since 0.0.15
     */
    default @NotNull Particle getParticle(@NotNull String key) {
        assertExists(key);
        String value = getExistentString(key);

        Particle particle = RegistryAccess.registryAccess().getRegistry(RegistryKey.PARTICLE_TYPE).get(asMinecraftKey(key));
        if (particle == null)
            throw new RuntimeException("Failed to parse Particle: " + value);

        return particle;
    }

    /**
     * Gets a Color from hex/rgb format in the config.
     * @param key String key
     * @return Color
     * @since 0.0.15
     */
    default @NotNull Color getColorFromString(@NotNull String key) {
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
            throw new RuntimeException("Failed to parse Color: '%s'; not in Hex (#rrggbb) or RGB (r,g,b) format.".formatted(value));
        }
    }

    /**
     * Gets a float from the config.
     * @param key String key
     * @return Float
     * @since 0.0.15
     */
    default float getFloat(@NotNull String key) {
        return Double.valueOf(getDouble(key)).floatValue();
    }

    /**
     * Gets a float from the config.
     * @param key String key
     * @return Float
     * @since 0.0.15
     */
    default float getFloat(@NotNull String key, float defaultValue) {
        return Double.valueOf(getDouble(key, defaultValue)).floatValue();
    }

    /**
     * Gets a Component from a MiniMessage-formatted String in the config.
     * Supports custom tags/colors added by ElvenideCore.
     * @return Component
     * @since 0.0.15
     */
    @Override
    default @Nullable Component getRichMessage(@NotNull String path) {
        return getRichMessage(path, null);
    }

    /**
     * Gets a Component from a MiniMessage-formatted String in the config, or a fallback.
     * Supports custom tags/colors added by ElvenideCore.
     * @return Component
     * @since 0.0.15
     */
    @Override
    @Nullable
    default Component getRichMessage(@NotNull String path, @Nullable Component fallback) {
        return getComponent(path, TextProvider.resolver(), fallback);
    }

    /**
     * Sets a Component as a MiniMessage-formatted String in the config.
     * Supports custom tags/colors added by ElvenideCore.
     * @since 0.0.15
     */
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
    default void remove(@NotNull String key) {
        set(key, null);
    }

    /**
     * Removes a value and immediately saves the config.
     * Best used when you only need to remove a single value before saving.
     * @param key String key
     * @since 0.0.15
     */
    default void removeAndSave(@NotNull String key) {
        remove(key);
        getRoot().save();
    }

    /**
     * Gets the root Config that this section is from.
     * @return Config
     */
    @Override
    @NotNull Config getRoot();

    /**
     * Gets the parent ConfigSection or Config of this section.
     * If this section is a Config, this will return null.
     * @return ConfigSection
     */
    @Override
    @Nullable AbstractSection getParent();

    /**
     * Creates and adds a ConfigSection to the config.
     * @param path Path to the ConfigSection
     * @return ConfigurationSection
     */
    @Override
    @NotNull AbstractSection createSection(@NotNull String path);

    /**
     * Creates and adds a ConfigSection to the config, with the given values.
     * @param path Path to the ConfigSection
     * @param map Map of keys and values to set
     * @return ConfigurationSection
     */
    @Override
    @NotNull AbstractSection createSection(@NotNull String path, @NotNull Map<?, ?> map);

    /**
     * Gets a ConfigSection from the config.
     * <p>
     * It is recommended to use {@link #getSection(String)} instead, for consistency and reduced verbosity.
     * @param path Path to the ConfigSection
     * @return ConfigSection, or <code>null</code> if not found
     */
    @ApiStatus.Experimental
    @Override
    @Nullable AbstractSection getConfigurationSection(@NotNull String path);

    /**
     * Gets a ConfigSection from the config.
     * <p>
     * Shorter alias for {@link #getConfigurationSection(String)}.
     * @param path Path to the ConfigSection
     * @return ConfigurationSection, or <code>null</code> if not found
     */
    default @Nullable AbstractSection getSection(@NotNull String path) {
        return getConfigurationSection(path);
    }

}
