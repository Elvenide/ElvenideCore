package com.elvenide.core.providers.config;

import com.elvenide.core.providers.TextProvider;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
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
     * This method is deprecated in favor of ElvenideCore alternatives.
     * Use {@link #root()} instead.
     * @return Configuration
     * @deprecated Use {@link #root()} instead.
     */
    @Deprecated(since = "0.0.15")
    @Override
    @Nullable Configuration getRoot();

    /**
     * This method is deprecated in favor of ElvenideCore alternatives.
     * Use {@link #parent()} instead.
     * @return ConfigurationSection
     * @deprecated Use {@link #parent()} instead.
     */
    @Deprecated(since = "0.0.15")
    @Override
    @Nullable ConfigurationSection getParent();

    /**
     * This method is deprecated in favor of ElvenideCore alternatives.
     * Use {@link #addSection(String)} instead.
     * @return ConfigurationSection
     * @deprecated Use {@link #addSection(String)}  instead.
     */
    @Deprecated(since = "0.0.15")
    @Override
    @NotNull ConfigurationSection createSection(@NotNull String path);

    /**
     * This method is deprecated in favor of ElvenideCore alternatives.
     * Use {@link #addSection(String, Map)} instead.
     * @return ConfigurationSection
     * @deprecated Use {@link #addSection(String, Map)} instead.
     */
    @Deprecated(since = "0.0.15")
    @Override
    @NotNull ConfigurationSection createSection(@NotNull String path, @NotNull Map<?, ?> map);

    /**
     * This method is deprecated in favor of ElvenideCore alternatives.
     * Use {@link #section(String)} instead.
     * @return ConfigurationSection
     * @deprecated Use {@link #section(String)} instead.
     */
    @Deprecated(since = "0.0.15")
    @Override
    @Nullable ConfigurationSection getConfigurationSection(@NotNull String path);

    /**
     * Gets a ConfigSection from the config.
     * @param key String key
     * @return ConfigSection (as AbstractSection)
     * @since 0.0.15
     */
    @Nullable AbstractSection section(@NotNull String key);

    /**
     * Creates and adds a ConfigSection to the config.
     * @param key String key
     * @return Created ConfigSection (as AbstractSection)
     * @since 0.0.15
     */
    default @NotNull AbstractSection addSection(@NotNull String key) {
        return addSection(key, Map.of());
    }

    /**
     * Creates and adds a ConfigSection to the config, with the given values.
     * @param key String key
     * @return Created ConfigSection (as AbstractSection)
     * @since 0.0.15
     */
    @NotNull AbstractSection addSection(@NotNull String key, @NotNull Map<?, ?> map);

    /**
     * Gets the parent ConfigSection or Config of this section.
     * If this section is a Config, this will return null.
     * @return ConfigSection (as AbstractSection)
     * @since 0.0.15
     */
    @Nullable AbstractSection parent();

    /**
     * Gets the root Config that this section is from.
     * @return Config
     * @since 0.0.15
     */
    @NotNull Config root();

}
