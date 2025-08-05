package com.elvenide.core.providers.config;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.plugin.CorePlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
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

    /// Allows Config to internally obtain plugin instance
    @ApiStatus.Internal
    JavaPlugin plugin() {
        return core.plugin;
    }

    @Override
    protected void ensureInitialized() throws IllegalStateException {
        // Ensure plugin is initialized
        super.ensureInitialized();

        // Ensure data folder exists
        File dataFolder = core.plugin.getDataFolder();
        if (!dataFolder.exists()) {
            boolean ignored = dataFolder.mkdirs();
        }
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
     * The config must have a resource at the resource path in your plugin code's <code>resources</code> folder.
     * If the config file does not exist, a new config will be created with the contents of the resource.
     * <p>
     * <b>Before using this</b>, you must do ONE of the following:
     * <ul>
     *     <li>Make your plugin extend {@link CorePlugin} (automatic initialization)</li>
     *     <li>{@link Core#setPlugin(JavaPlugin) Manual initialization}</li>
     * </ul>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @param resourcePath The path, relative to your code's resources folder (e.g. "./config.yml")@
     * @return The config
     */
    @PublicAPI
    public Config get(@NotNull String relativePath, @NotNull String resourcePath) {
        ensureInitialized();

        // Remove leading "./"
        if (resourcePath.startsWith("./"))
            relativePath = relativePath.substring(2);

        // Get any existing config
        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        // Create config with resource
        File file = new File(core.plugin.getDataFolder(), relativePath);
        Config config = new Config(this, file, resourcePath);
        configs.put(relativePath, config);
        return config;
    }
}
