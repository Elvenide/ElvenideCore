package com.elvenide.core;

import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.command.CommandProvider;
import com.elvenide.core.providers.config.ConfigProvider;
import com.elvenide.core.providers.item.ItemProvider;
import com.elvenide.core.providers.key.KeyProvider;
import com.elvenide.core.providers.lang.LangProvider;
import com.elvenide.core.providers.perm.PermProvider;
import com.elvenide.core.providers.plugin.CorePlugin;
import com.elvenide.core.providers.task.TaskProvider;
import com.elvenide.core.providers.text.TextProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * ElvenideCore is a powerful library containing numerous features useful in nearly every plugin.
 * @author <a href="https://elvenide.com">Elvenide</a>
 */
@PublicAPI
@ApiStatus.NonExtendable
public class Core {
    private static int counter = 0;

    @ApiStatus.Internal
    public JavaPlugin plugin;

    @ApiStatus.Internal
    Core() { increment(); }
    private static final Core INSTANCE = new Core();
    private void increment() {
        if (counter > 0)
            throw new IllegalStateException("ElvenideCore is already initialized");
        counter++;
    }

    /**
     * Sets the plugin that is using ElvenideCore.
     * <p>
     * Required for some features, unless your plugin extends the {@link CorePlugin} class
     * (which automatically calls this method).
     * @param plugin Plugin
     */
    @PublicAPI
    public static void setPlugin(JavaPlugin plugin) {
        INSTANCE.plugin = plugin;
    }

    /**
     * Easily convert between Strings and Components, with support for custom tags.
     * @since 0.0.1
     */
    @PublicAPI
    public static final TextProvider text = new TextProvider(INSTANCE);

    /**
     * Easily access and manipulate YAML configuration files.
     * @since 0.0.2
     */
    @PublicAPI
    public static final ConfigProvider config = new ConfigProvider(INSTANCE);

    /**
     * Define your plugin's messaging in a central system, then access it in any MiniMessage format
     * text using <code>&lt;elang&gt;</code> tags.
     * @since 0.0.3
     */
    @PublicAPI
    public static final LangProvider lang = new LangProvider(INSTANCE);
    static { lang.common = new LangProvider.CommonLangKeys(); }

    /**
     * Check different kinds of permissions with extreme ease.
     * @since 0.0.5
     */
    @PublicAPI
    public static final PermProvider perms = new PermProvider(INSTANCE);

    /**
     * Easily create commands that take full advantage of Minecraft's brigadier command API.
     * @since 0.0.8
     */
    @PublicAPI
    public static final CommandProvider commands = new CommandProvider(INSTANCE);

    /**
     * Easily manage your plugin's NamespacedKeys.
     * <p>
     * To function, this feature requires ONE of the following:
     * <ul>
     *     <li>Use of plugin extending {@link CorePlugin} (automatic initialization)</li>
     *     <li>{@link #setPlugin(JavaPlugin) Manual initialization}</li>
     * </ul>
     * @since 0.0.14
     */
    @PublicAPI
    public static final KeyProvider keys = new KeyProvider(INSTANCE);

    /**
     * Easily create and manipulate items.
     * @since 0.0.15
     */
    @PublicAPI
    public static final ItemProvider items = new ItemProvider(INSTANCE);

    /**
     * Schedule tasks to run delayed or periodically.
     * @since 0.0.15
     */
    @PublicAPI
    public static final TaskProvider tasks = new TaskProvider(INSTANCE);
}
