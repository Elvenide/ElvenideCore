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
