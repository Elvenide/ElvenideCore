package com.elvenide.core.providers.config;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.event.builtin.CoreReloadEvent;
import com.elvenide.core.providers.plugin.PluginProvider;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class should not be directly referenced by any plugin.
 * Its methods should only be utilized through the {@link Core#config} field.
 */
public class ConfigProvider extends Provider {
    private final HashMap<String, Config> configs = new HashMap<>();
    private final HashSet<ConfigSupplier> configSuppliers = new HashSet<>();

    @ApiStatus.Internal
    public ConfigProvider(@Nullable Core core) {
        super(core);
    }

    @Override
    protected void ensureInitialized() throws IllegalStateException {
        // Ensure plugin is initialized
        super.ensureInitialized();

        // Ensure data folder exists
        File dataFolder = Core.plugin.get().getDataFolder();
        if (!dataFolder.exists()) {
            boolean ignored = dataFolder.mkdirs();
        }
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @return The config
     */
    @PublicAPI
    public @NotNull Config get(@NotNull String relativePath) {
        ensureInitialized();

        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        File file = new File(Core.plugin.get().getDataFolder(), relativePath);
        Config config = new Config(file);
        configs.put(relativePath, config);
        return config;
    }

    /**
     * Deletes a config file at the provided path relative to your plugin's data folder.
     * Note that this method can delete any file and does not explicitly check for config files.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     */
    @PublicAPI
    public void deleteFile(@NotNull String relativePath) {
        ensureInitialized();

        File file = new File(Core.plugin.get().getDataFolder(), relativePath);
        boolean ignored = file.delete();
        configs.remove(relativePath);
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * The config must have a resource at the resource path in your plugin code's <code>resources</code> folder.
     * If the config file does not exist, a new config will be created with the contents of the resource.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
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
        File file = new File(Core.plugin.get().getDataFolder(), relativePath);
        Config config = new Config(file, resourcePath);
        configs.put(relativePath, config);
        return config;
    }

    /**
     * Registers one or more config suppliers.
     * Registered config suppliers will be automatically reloaded when {@link #reloadSuppliers()} is called.
     * @param suppliers Config suppliers
     * @since 0.0.17
     */
    @PublicAPI
    public final void registerSuppliers(ConfigSupplier... suppliers) {
        configSuppliers.addAll(List.of(suppliers));
    }

    /**
     * Manually reloads all registered {@link #registerSuppliers(ConfigSupplier...) config suppliers}.
     * Emits a {@link CoreReloadEvent} and reloads all registered {@link #registerSuppliers(ConfigSupplier...) config suppliers}.
     * @since 0.0.17
     */
    @PublicAPI
    public void reloadSuppliers() {
        // Reload config suppliers
        for (ConfigSupplier supplier : configSuppliers)
            supplier.reload();

        // Emit reload event
        CoreReloadEvent event = new CoreReloadEvent();
        event.callEvent(); // Emit Bukkit event
        event.callCoreEvent(); // Emit ElvenideCore event
    }

    /**
     * Creates a config section that is not part of any config file.
     * The orphaned section's parent is always <code>null</code> and its root cannot be saved or reloaded.
     * @param entries Key paths mapped to their values
     * @return ConfigSection
     * @apiNote
     * For nested sections in the entries, use dot notation keys instead of a single entry for the nested section.
     * For example, instead of mapping "section_a" to a ConfigSection containing "key" with value 1,
     * use "section_a.key" mapped to value 1.
     */
    @PublicAPI
    public ConfigSection createOrphanedSection(Map<String, ?> entries) {
        MemoryConfiguration section = new MemoryConfiguration();
        for (String key : entries.keySet())
            section.set(key, entries.get(key));
        return new ConfigSectionImpl(section, null, new OrphanedConfigImpl());
    }
}
