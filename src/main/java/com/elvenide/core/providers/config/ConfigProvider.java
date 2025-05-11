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
    public @NotNull Config get(String relativePath) {
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
    public void deleteFile(String relativePath) {
        ensureInitialized();

        File file = new File(core.plugin.getDataFolder(), relativePath);
        boolean ignored = file.delete();
        configs.remove(relativePath);
    }
}
