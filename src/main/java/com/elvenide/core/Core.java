package com.elvenide.core;

import com.elvenide.core.events.CoreCancellable;
import com.elvenide.core.events.CoreEvent;
import com.elvenide.core.events.CoreEventHandler;
import com.elvenide.core.events.CoreListener;
import com.elvenide.core.plugin.CorePlugin;
import com.elvenide.core.providers.*;
import com.elvenide.core.providers.command.CommandProvider;
import com.elvenide.core.providers.config.ConfigProvider;
import com.elvenide.core.providers.item.ItemProvider;
import com.elvenide.core.providers.lang.LangProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * ElvenideCore is a powerful library containing numerous features useful in nearly every plugin.
 * @author <a href="https://elvenide.com">Elvenide</a>
 */
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
    public static void setPlugin(JavaPlugin plugin) {
        INSTANCE.plugin = plugin;
    }

    /**
     * Easily convert between Strings and Components, with support for custom tags.
     * @since 0.0.1
     */
    public static final TextProvider text = new TextProvider(INSTANCE);

    /**
     * Easily access and manipulate YAML configuration files.
     * @since 0.0.2
     */
    public static final ConfigProvider config = new ConfigProvider(INSTANCE);

    /**
     * Define your plugin's messaging in a central system, then access it in any MiniMessage format
     * text using <code>&lt;elang&gt;</code> tags.
     * @since 0.0.3
     */
    public static final LangProvider lang = new LangProvider(INSTANCE);
    static { lang.common = new LangProvider.CommonLangKeys(); }

    /**
     * Check different kinds of permissions with extreme ease.
     * @since 0.0.5
     */
    public static final PermissionProvider perms = new PermissionProvider(INSTANCE);

    /**
     * Easily create commands that take full advantage of Minecraft's brigadier command API.
     * @since 0.0.8
     */
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
    public static final KeyProvider keys = new KeyProvider(INSTANCE);

    /**
     * Easily create and manipulate items.
     * @since 0.0.15
     */
    public static final ItemProvider items = new ItemProvider(INSTANCE);

    /**
     * Schedule tasks to run delayed or periodically.
     * @since 0.0.15
     */
    public static final TaskProvider tasks = new TaskProvider(INSTANCE);

    /**
     * ElvenideCore's powerful extension of the {@link JavaPlugin} class.
     * @author <a href="https://elvenide.com">Elvenide</a>
     * @since 0.0.15
     */
    public static abstract class Plugin extends CorePlugin {}

    /**
     * Represents a custom ElvenideCore event that can be handled by registered {@link CoreListener CoreListener}s.
     * <p>
     * This interface is a powerful alternative to the Bukkit event system for custom events, as it:
     * <ul>
     *     <li>Eliminates 99% of event boilerplate code</li>
     *     <li>Allows use of <code>record</code> classes to further reduce verbosity</li>
     *     <li>Follows familiar, Bukkit-like format</li>
     * </ul>
     * @author <a href="https://elvenide.com">Elvenide</a>
     * @since 0.0.15
     */
    public interface Event extends CoreEvent {}

    /**
     * Represents a {@link CoreEvent} that can be cancelled.
     * @since 0.0.15
     * @author <a href="https://elvenide.com">Elvenide</a>
     */
    public interface CancellableEvent extends CoreEvent, CoreCancellable {}

    /**
     * Represents a listener that can handle CoreEvent events,
     * including both built-in ElvenideCore events and your own custom events.
     * <p>
     * Individual methods in the class implementing this interface should be annotated
     * with {@link CoreEventHandler @CoreEventHandler} to allow them to receive events, similar to Bukkit's {@link org.bukkit.event.Listener}.
     * @author <a href="https://elvenide.com">Elvenide</a>
     * @since 0.0.15
     */
    public interface Listener extends CoreListener {}
}
