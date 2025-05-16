package com.elvenide.core.providers.config;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.plugin.CorePlugin;
import com.google.common.base.Charsets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#config} field.
 */
public class ConfigProvider extends Provider {
    private final HashMap<String, Config> configs = new HashMap<>();

    @ApiStatus.Internal
    public ConfigProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * @param plugin Your plugin
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @return The config
     * @deprecated Use {@link #get(String)} with initialization instead.
     */
    @Deprecated(since = "0.0.15", forRemoval = true)
    @PublicAPI
    public @NotNull Config get(JavaPlugin plugin, String relativePath) {
        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        File file = new File(plugin.getDataFolder(), relativePath);
        Config config = new Config(this, file);
        configs.put(relativePath, config);
        return config;
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * <p>
     * <b>Before using this</b>, you must do ONE of the following:
     * <ul>
     *     <li>Make your plugin extend {@link CorePlugin} (automatic initialization)</li>
     *     <li>{@link Core#setPlugin(JavaPlugin) Manual initialization}</li>
     * </ul>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @return The config
     */
    @PublicAPI
    public @NotNull Config get(@NotNull String relativePath) {
        ensureInitialized();

        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        File file = new File(core.plugin.getDataFolder(), relativePath);
        Config config = new Config(this, file);
        configs.put(relativePath, config);
        return config;
    }

    /**
     * Deletes a config file at the provided path relative to your plugin's data folder.
     * Note that this method can delete any file and does not explicitly check for config files.
     * <p>
     * <b>Before using this</b>, you must do ONE of the following:
     * <ul>
     *     <li>Make your plugin extend {@link CorePlugin} (automatic initialization)</li>
     *     <li>{@link Core#setPlugin(JavaPlugin) Manual initialization}</li>
     * </ul>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     */
    @PublicAPI
    public void deleteFile(@NotNull String relativePath) {
        ensureInitialized();

        File file = new File(core.plugin.getDataFolder(), relativePath);
        boolean ignored = file.delete();
        configs.remove(relativePath);
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * The config must have a resource with the same path in your plugin code's <code>resources</code> folder.
     * If the config file does not exist, a new config will be created with the contents of the resource.
     * <p>
     * <b>Before using this</b>, you must do ONE of the following:
     * <ul>
     *     <li>Make your plugin extend {@link CorePlugin} (automatic initialization)</li>
     *     <li>{@link Core#setPlugin(JavaPlugin) Manual initialization}</li>
     * </ul>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @return The config
     */
    @PublicAPI
    public Config getResource(@NotNull String relativePath) {
        ensureInitialized();

        // Remove leading "./"
        if (relativePath.startsWith("./"))
            relativePath = relativePath.substring(2);

        // Get any existing config
        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        // Load the resource
        InputStream stream = core.plugin.getResource(relativePath);
        if (stream == null)
            throw new RuntimeException("Failed to load resource: " + relativePath);

        // Determine if the file exists
        File file = new File(core.plugin.getDataFolder(), relativePath);
        boolean isNew = !file.exists();

        // Get config, copying resource if it doesn't exist
        Config config = get(relativePath);
        if (isNew) {
            try {
                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            config.reload();
        }

        // Load defaults
        config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, Charsets.UTF_8)));
        return config;
    }
}
