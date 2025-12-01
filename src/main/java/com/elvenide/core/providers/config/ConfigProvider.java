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
import java.util.*;

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

    private String normalizePath(@NotNull String path) {
        // Remove leading "./"
        if (path.startsWith("./"))
            path = path.substring(2);
        return path;
    }

    private Config getConfigFile(@NotNull File parent, @NotNull String relativePath, @Nullable String resourcePath) {
        relativePath = relativePath.replace("/", File.separator);

        File file = new File(parent, relativePath);
        Config config = resourcePath == null ? new Config(file) : new Config(file, resourcePath);
        configs.put(relativePath, config);
        return config;
    }

    private List<Reloadable> getConfigDir(@NotNull File parent, @NotNull String relativeDirPath, boolean deep) {
        relativeDirPath = relativeDirPath.replace("/", File.separator);

        File dir = new File(parent, relativeDirPath);
        if (!dir.exists()) {
            boolean ignored = dir.mkdirs();
            if (!dir.isDirectory())
                throw new IllegalArgumentException("The given config directory path is not a directory: " + relativeDirPath);
        }

        List<Reloadable> configs = new ArrayList<>();
        File @NotNull [] files = Objects.requireNonNull(dir.listFiles());
        String sep = File.separator;

        for (File file : files) {
            if (file.isDirectory() && deep)
                configs.addAll(getConfigDir(parent, relativeDirPath + sep + file.getName(), true));
            else if (!file.isDirectory() && file.getName().toLowerCase().endsWith(".yml"))
                configs.add(getConfigFile(Core.plugin.get().getDataFolder(), relativeDirPath + sep + file.getName(), null));
        }
        return configs;
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
        relativePath = normalizePath(relativePath);
        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        return getConfigFile(Core.plugin.get().getDataFolder(), relativePath, null);
    }

    /**
     * Gets a config in the provided path relative to your plugin's data folder.
     * The config must have a resource at the resource path in your plugin code's <code>resources</code> folder.
     * If the config file does not exist, a new config will be created with the contents of the resource.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
     * @param relativePath The path, relative to your plugin's data folder (e.g. "./config.yml")
     * @param resourcePath The path, relative to your code's resources folder (e.g. "./config.yml")
     * @return The config
     */
    @PublicAPI
    public Config get(@NotNull String relativePath, @NotNull String resourcePath) {
        ensureInitialized();
        relativePath = normalizePath(relativePath);
        resourcePath = normalizePath(resourcePath);
        if (configs.containsKey(relativePath))
            return configs.get(relativePath);

        return getConfigFile(Core.plugin.get().getDataFolder(), relativePath, resourcePath);
    }

    /**
     * Gets all YAML configs in the provided directory path relative to your plugin's data folder.
     * <p>
     * <b>To function, this feature requires initialization through {@link PluginProvider#set(JavaPlugin) Core.plugin.set()}.</b>
     * @param relativeDirPath The directory path, relative to your plugin's data folder (e.g. "./folder")
     * @param deep If true, all YAML configs in nested subdirectories will also be recursively included
     * @return A supplier of all configs in the directory
     * @since 0.0.19
     * @apiNote
     * Unlike {@link #get(String)}, this method does not cache the resulting Config objects
     * and always returns new instances.
     */
    @PublicAPI
    public ConfigSupplier getDir(@NotNull String relativeDirPath, boolean deep) {
        ensureInitialized();
        relativeDirPath = normalizePath(relativeDirPath);

        final List<Reloadable> configs = getConfigDir(Core.plugin.get().getDataFolder(), relativeDirPath, deep);
        return () -> configs;
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
