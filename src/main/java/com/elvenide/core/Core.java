package com.elvenide.core;

import com.elvenide.core.api.PublicAPI;
import com.elvenide.core.providers.command.CommandProvider;
import com.elvenide.core.providers.config.ConfigProvider;
import com.elvenide.core.providers.config.ConfigSection;
import com.elvenide.core.providers.item.ItemProvider;
import com.elvenide.core.providers.key.KeyProvider;
import com.elvenide.core.providers.lang.LangKey;
import com.elvenide.core.providers.log.LogProvider;
import com.elvenide.core.providers.perm.PermProvider;
import com.elvenide.core.providers.plugin.PluginProvider;
import com.elvenide.core.providers.task.TaskProvider;
import com.elvenide.core.providers.text.TextProvider;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
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
    Core() {
        increment();
    }
    private static final Core INSTANCE = new Core();
    private void increment() {
        if (counter > 0)
            throw new IllegalStateException("ElvenideCore is already initialized");
        counter++;
    }

    /**
     * @deprecated Use {@link PluginProvider#set(JavaPlugin) Core.plugin.set()} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public static void setPlugin(JavaPlugin plugin) {
        Core.plugin.set(plugin);
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
     * Easily manage your plugin's NamespacedKeys and GoalKeys.
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

    /**
     * Send various types of log messages to the console, with the ability to easily toggle logging on and off.
     * @since 0.0.15
     */
    @PublicAPI
    public static final LogProvider log = new LogProvider(INSTANCE);

    /**
     * Manage the ElvenideCore plugin instance, and utilize various plugin-dependent utilities.
     * @since 0.0.17
     */
    @PublicAPI
    public static final PluginProvider plugin = new PluginProvider(INSTANCE);

    /**
     * @deprecated Use {@link PluginProvider#registerListeners(Listener...) Core.plugin.registerListeners()} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public static void registerListener(Listener listener) {
        Core.plugin.registerListeners(listener);
    }

    /**
     * @deprecated Compare the plugin with {@link PluginProvider#get() Core.plugin.get()} instead.
     *             For example: <code>Core.plugin.get() == somePlugin</code>
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public static boolean isYourPlugin(Plugin plugin) {
        return Core.plugin.get() == plugin;
    }

    /**
     * Built-in language keys to customize and configure plugin messaging.
     * Some can be used to customize default messages for ElvenideCore.
     * A few common keys are also provided for use by your plugin.
     * @since 0.0.17
     */
    @PublicAPI
    public enum lang implements LangKey {
        /**
         * Error message shown to non-player command senders trying to use player-only commands.
         * @since 0.0.8
         */
        @PublicAPI
        NOT_PLAYER("<red>You must be a player to use this."),

        /**
         * Error message shown to command senders trying to use commands they do not have permission for.
         * @since 0.0.8
         */
        @PublicAPI
        NO_PERMISSION("<red>You do not have permission to use this."),

        /**
         * The command header, displayed above command usage information.
         * As of v0.0.16, by default, this is a red gradient with the name of your plugin.
         * @since 0.0.8
         */
        @PublicAPI
        COMMAND_HEADER("<gradient:red:dark_red>{}"),

        /**
         * The command usage prefix, displayed at the start of each command help line.
         * Can be used to color the slash (/) in the command usage display.
         * @since 0.0.10
         */
        @PublicAPI
        COMMAND_USAGE_PREFIX("<gray>"),

        /**
         * Error message shown when a required argument is missing.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        @PublicAPI
        MISSING_ARGUMENT("Missing argument: '%s'. Hover to see command syntax."),

        /**
         * Error message shown when an argument has an invalid datatype.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        @PublicAPI
        INVALID_TYPE("Invalid value provided for argument: '%s'. Hover to see command syntax."),

        /**
         * Error message shown when an argument specifies an invalid player.
         * Has a single String placeholder (%s).
         * @since 0.0.13
         */
        @PublicAPI
        INVALID_PLAYER("Invalid player selector/username provided for argument: '%s'. Hover to see command syntax."),

        /**
         * The formatting of subgroups and base-level commands in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        SUBGROUP_HELP_FORMATTING("<gray>{}</gray>"),

        /**
         * The formatting of subcommands in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        SUBCOMMAND_HELP_FORMATTING("<gray>{}</gray>"),

        /**
         * The formatting of boolean arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        BOOL_ARGUMENT_HELP_FORMATTING("<gold>{}</gold>"),

        /**
         * The formatting of string and unknown arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        STRING_ARGUMENT_HELP_FORMATTING("<dark_green>{}</dark_green>"),

        /**
         * The formatting of numeric arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        NUMBER_ARGUMENT_HELP_FORMATTING("<dark_aqua>{}</dark_aqua>"),

        /**
         * The formatting of player arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        PLAYER_ARGUMENT_HELP_FORMATTING("<dark_purple>{}</dark_purple>"),

        /**
         * The formatting of item arguments in the command help message.
         * Has a single String placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        ITEM_ARGUMENT_HELP_FORMATTING("<blue>{}</blue>"),

        /**
         * Message shown when commands are hidden in the help message due to missing permissions.
         * Has a single Integer placeholder ({}).
         * @since 0.0.15
         */
        @PublicAPI
        COMMANDS_HIDDEN_BY_PERMS("<dark_gray>{} commands were hidden due to lack of permissions."),

        /**
         * Customizable START message provided for your plugin.
         * Not used by ElvenideCore.
         * @since 0.0.17
         */
        @PublicAPI
        START("<green>Starting..."),

        /**
         * Customizable STOP message provided for your plugin.
         * Not used by ElvenideCore.
         * @since 0.0.17
         */
        @PublicAPI
        STOP("<red>Stopping..."),

        /**
         * Customizable RESTART message provided for your plugin.
         * Not used by ElvenideCore.
         * @since 0.0.17
         */
        @PublicAPI
        RESTART("<yellow>Restarting..."),

        /**
         * Customizable RELOAD message provided for your plugin.
         * Not used by ElvenideCore.
         * @since 0.0.17
         */
        @PublicAPI
        RELOAD("<gold>Reloading..."),


        /**
         * Customizable JOIN message provided for your plugin.
         * Not used by ElvenideCore.
         * Has a single String placeholder ({}).
         * @since 0.0.17
         */
        @PublicAPI
        JOIN("<green>{} joined the game."),


        /**
         * Customizable LEAVE message provided for your plugin.
         * Not used by ElvenideCore.
         * Has a single String placeholder ({}).
         * @since 0.0.17
         */
        @PublicAPI
        LEAVE("<red>{} left the game."),

        ;

        lang(String value) {
            set(value);
        }

        /**
         * Imports lang keys from a config into an array of LangKey enum values.
         * @apiNote
         * If you have an enum class that implements <code>LangKey</code> (e.g. named <code>YourEnum</code>),
         * you can get an array of LangKey enum values using <code>YourEnum.values()</code>
         * for use as the second argument to this method.
         * @param langConfig The config or config section to import lang key values from
         * @param langKeyEnums Enum values to store lang key values in
         * @throws IllegalArgumentException If any of the provided enums do not implement LangKey
         * @since 0.0.17
         */
        @PublicAPI
        public static void fromConfig(ConfigSection langConfig, Enum<?>[] langKeyEnums) throws IllegalArgumentException {
            for (Enum<?> enumKey : langKeyEnums) {
                if (!(enumKey instanceof LangKey langKey))
                    throw new IllegalArgumentException("To import lang keys from config into enums, the provided enums must implement LangKey.");

                String value = langConfig.getString(enumKey.name());
                if (value != null)
                    langKey.set(value);
            }
        }
    }
}
