package com.elvenide.core.providers;

import com.elvenide.core.ElvenideCore;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.config.Config;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;

public class ConfigProvider extends Provider {
    private final HashMap<String, Config> configs = new HashMap<>();

    @ApiStatus.Internal
    public ConfigProvider(@Nullable ElvenideCore core) {
        super(core);
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * @param plugin Your plugin
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @return The config
     */
    public @NotNull Config get(JavaPlugin plugin, String relativePath) {
        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        File file = new File(plugin.getDataFolder(), relativePath);
        Config config = new Config(this, file);
        configs.put(relativePath, config);
        return config;
    }
}
