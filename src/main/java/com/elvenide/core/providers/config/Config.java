package com.elvenide.core.providers.config;

import com.elvenide.core.providers.ConfigProvider;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
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

    @Override
    public @NotNull Config getRoot() {
        return this;
    }

    @Contract("-> null")
    @Override
    public @Nullable AbstractSection getParent() {
        return null;
    }

    @Override
    public @NotNull AbstractSection createSection(@NotNull String path) {
        return new ConfigSection(super.createSection(path), this, this);
    }

    @Override
    public @NotNull AbstractSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return new ConfigSection(super.createSection(path, map), this, this);
    }

    @ApiStatus.Experimental
    @Override
    public @Nullable AbstractSection getConfigurationSection(@NotNull String path) {
        if (isConfigurationSection(path))
            return new ConfigSection(Objects.requireNonNull(super.getConfigurationSection(path)), this, this);
        return null;
    }
}
