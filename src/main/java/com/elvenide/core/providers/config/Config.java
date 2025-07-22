package com.elvenide.core.providers.config;

import com.elvenide.core.api.PublicAPI;
import com.google.common.base.Charsets;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;

public class Config extends YamlConfiguration implements ConfigSection {

    private final ConfigProvider system;
    private final File file;
    private final @Nullable String resourcePath;

    Config(@Nullable ConfigProvider system, File file) {
        super();

        if (system == null)
            throw new IllegalArgumentException("ConfigSystem cannot be null");

        this.system = system;
        this.file = file;
        this.resourcePath = null;

        reload();
    }

    Config(@Nullable ConfigProvider system, File file, @NotNull String resource) {
        super();

        if (system == null)
            throw new IllegalArgumentException("ConfigSystem cannot be null");

        this.system = system;
        this.file = file;
        this.resourcePath = resource;

        reload();
    }

    /**
     * Reloads the config from the file.
     * Creates the file and its parent folders if they don't exist.
     */
    @PublicAPI
    public void reload() {
        InputStream resource = resourcePath != null ? system.plugin().getResource(resourcePath) : null;
        if (resourcePath != null && resource == null)
            throw new RuntimeException("Failed to load resource: " + resourcePath);

        if (!file.getParentFile().exists()) {
            boolean ignored = file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                boolean ignored = file.createNewFile();
            } catch (Exception ignored1) {
                throw new RuntimeException("Failed to create config file: " + file.getAbsolutePath());
            }

            if (resource != null)
                try {
                    Files.copy(resource, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy resource to config file: " + file.getAbsolutePath(), e);
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

        if (resource != null)
            setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(resource, Charsets.UTF_8)));
    }

    /**
     * Saves the config to the file.
     */
    @PublicAPI
    public void save() {
        try {
            save(file);
        } catch (IOException ignored) {
            throw new RuntimeException("Failed to save config file: " + file.getAbsolutePath());
        }
    }

    @PublicAPI
    @Override
    public @NotNull Config getRoot() {
        return this;
    }

    @PublicAPI
    @Contract("-> null")
    @Override
    public @Nullable ConfigSection getParent() {
        return null;
    }

    @PublicAPI
    @Override
    public @NotNull ConfigSection createSection(@NotNull String path) {
        return new ConfigSectionImpl(super.createSection(path), this, this);
    }

    @PublicAPI
    @Override
    public @NotNull ConfigSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return new ConfigSectionImpl(super.createSection(path, map), this, this);
    }

    @PublicAPI
    @ApiStatus.Experimental
    @Override
    public @Nullable ConfigSection getConfigurationSection(@NotNull String path) {
        if (isConfigurationSection(path))
            return new ConfigSectionImpl(Objects.requireNonNull(super.getConfigurationSection(path)), this, this);
        return null;
    }
}
