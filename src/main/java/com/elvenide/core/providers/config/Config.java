package com.elvenide.core.providers.config;

import com.elvenide.core.providers.ConfigProvider;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Config extends YamlConfiguration implements AbstractSection {

    private final File file;
    public Config(@Nullable ConfigProvider system, File file) {
        super();

        if (system == null)
            throw new IllegalArgumentException("ConfigSystem cannot be null");

        this.file = file;
        reload();
    }

    /**
     * Reloads the config from the file.
     * Creates the file and its parent folders if they don't exist.
     */
    public void reload() {
        if (!file.getParentFile().exists()) {
            boolean ignored = file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                boolean ignored = file.createNewFile();
            } catch (Exception ignored1) {
                throw new RuntimeException("Failed to create config file: " + file.getAbsolutePath());
            }
        }

        try {
            load(file);
        }
        catch (IOException ignored) {
            throw new RuntimeException("Failed to load config file: " + file.getAbsolutePath());
        }
        catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the config to the file.
     */
    public void save() {
        try {
            save(file);
        } catch (IOException ignored) {
            throw new RuntimeException("Failed to save config file: " + file.getAbsolutePath());
        }
    }

    /**
     * Sets a value and immediately saves the config.
     * Best used when you only need to set a single value before saving.
     * @param key String key
     * @param value Value
     */
    public void setAndSave(@NotNull String key, @Nullable Object value) {
        set(key, value);
        save();
    }

    /**
     * Removes a value from the config.
     * Simply a clearer alias for <code>set(key, null)</code>.
     * @param key String key
     */
    public void remove(@NotNull String key) {
        set(key, null);
    }

    /**
     * Removes a value and immediately saves the config.
     * @param key String key
     */
    public void removeAndSave(@NotNull String key) {
        remove(key);
        save();
    }

    /**
     * @since 0.0.15
     */
    @Override
    public @Nullable AbstractSection section(@NotNull String key) {
        if (!isConfigurationSection(key))
            return null;
        return new ConfigSection(Objects.requireNonNull(getConfigurationSection(key)), this, this);
    }

    /**
     * @since 0.0.15
     */
    @Override
    public @NotNull AbstractSection addSection(@NotNull String key, @NotNull Map<?, ?> map) {
        return new ConfigSection(createSection(key, map), this, this);
    }

    /**
     * Returns <code>null</code>, as a Config does not have a parent.
     * @return Null
     * @since 0.0.15
     */
    @Override
    public @Nullable AbstractSection parent() {
        return null;
    }

    /**
     * Gets this Config instance.
     * @return This
     * @since 0.0.15
     */
    @Override
    public @NotNull Config root() {
        return this;
    }
}
