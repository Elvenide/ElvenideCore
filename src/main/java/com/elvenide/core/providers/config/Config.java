package com.elvenide.core.providers.config;

import com.elvenide.core.providers.ConfigProvider;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Config extends YamlConfiguration {

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
        } catch (IOException | InvalidConfigurationException ignored) {
            throw new RuntimeException("Failed to load config file: " + file.getAbsolutePath());
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
     * Gets a URI from the config.
     * @param key String key
     * @return URI
     */
    public @Nullable URI getURI(@NotNull String key) {
        if (!contains(key))
            return null;

        String value = getString(key);
        if (value == null)
            return null;

        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse URI: " + value, e);
        }
    }

}
