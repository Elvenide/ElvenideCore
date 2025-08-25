# ElvenideCore
ElvenideCore is a powerful library for PaperMC plugins, primarily used in many of my own plugins.
It is not a standalone plugin, and must be shaded into your own plugin.

## Versions
- [v0.0.16](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.16) for Paper 1.21.8 (latest)
- [v0.0.16-1.21.4](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.16-1.21.4) for Paper 1.21.4 (latest)
- [v0.0.15](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.15) for Paper 1.21.8
- [v0.0.15-1.21.4](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.15-1.21.4) for Paper 1.21.4
- [v0.0.14](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.14) for Paper 1.21.4
- [v0.0.13](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.13) for Paper 1.21.4

Note: Only the latest version of Paper at the time of the latest ElvenideCore release is guaranteed to be supported. Outdated versions of Paper (such as 1.21.4) will eventually lose support.

## Installation

If you are using Maven, add the following to your pom.xml:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

Then add the following dependency:
```xml
<dependency>
    <groupId>com.elvenide</groupId>
    <artifactId>ElvenideCore</artifactId>
    <version>0.0.16</version>
    <scope>compile</scope>
</dependency>
```

And add the following to your pom.xml to shade ElvenideCore into your plugin jar (replacing `com.your.path` with your own package path):
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.5.3</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>com.elvenide.core</pattern>
                        <shadedPattern>com.your.path.elvenide.core</shadedPattern>
                    </relocation>
                </relocations>
                <filters>
                    <filter>
                        <artifact>*:*</artifact>
                        <excludes>
                            <exclude>META-INF/**</exclude>
                        </excludes>
                    </filter>
                </filters>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Documentation
Select the version of ElvenideCore you would like to view the documentation for.

- [v0.0.17](#getting-started)
- [v0.0.16](https://github.com/Elvenide/ElvenideCore/blob/0.0.16/README.md#getting-started)
- [v0.0.15](https://github.com/Elvenide/ElvenideCore/blob/0.0.15/README.md#getting-started)
- v0.0.14 - no in-depth documentation
- v0.0.13 and earlier - no documentation

## Getting Started
You can now start using ElvenideCore.

### Initializing with Plugin Provider (new in v0.0.17)
Initialize your plugin using the `Core.plugin.set(JavaPlugin)` method.
This replaces the previous method of initializing via `CorePlugin`, which is now deprecated as of v0.0.17.

```java
import com.elvenide.core.Core;
import org.bukkit.plugin.java.JavaPlugin;

public class YourPlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    // Initialize ElvenideCore with your plugin
    Core.plugin.set(this);
    
    // ... {your plugin start up functionality here}
    
    // If you have commands, register them here
    Core.commands.register();
  }

  @Override
  public void onDisable() {
    // Plugin shut down functionality
  }
}
```

Features:
- Initialize ElvenideCore using `Core.plugin.set(JavaPlugin)`
- Easily get your initialized plugin instance anywhere using `Core.plugin.get()`
- Static methods to register/unregister Bukkit listeners without needing to give your plugin as an argument

### CoreEvent
Creating custom events using the Bukkit event system is very verbose, with lots of unnecessary boilerplate.
ElvenideCore provides the `CoreEvent` interface, which is a powerful, simple, yet familiar alternative for custom events.

Example event class:

```java
import com.elvenide.core.providers.event.CoreEvent;
import org.bukkit.entity.Player;

public record YourCustomEvent(Player player, String message) implements CoreEvent {
    // YUP, that's all.
    // No need for constructors
    // No need for handler lists
    // No need for getter methods and private fields
}
```

If you want your event to be cancellable, just add the `CoreCancellable` interface:

```java
import com.elvenide.core.providers.event.CoreEvent;
import com.elvenide.core.providers.event.CoreCancellable;
import org.bukkit.entity.Player;

public record YourCustomEvent(Player player, String message) implements CoreEvent, CoreCancellable {
    // YUP, that's all.
    // No need for setCancelled()
    // No need for isCancelled()
}
```

To call your event:
```java
Player player = /* ... */;
String message = /* ... */;
new YourCustomEvent(player, message).callCoreEvent();
```

To create a listener for your event:

```java
import com.elvenide.core.providers.event.CoreEventPriority;
import com.elvenide.core.providers.event.CoreListener;
import com.elvenide.core.providers.event.CoreEventHandler;

public class YourListener implements CoreListener {
    @CoreEventHandler(priority = CoreEventPriority.NORMAL)
    public void onYourCustomEvent(YourCustomEvent event) {
        // Example: send the message to the player
        event.player().sendRichMessage(event.message());
        
        // Example: cancel the event for other, later-priority listeners
        event.setCancelled(true);
    }
}
```

Register your listener:

```java
new YourListener().register();
```

Features:
- Custom events that eliminate 99% of Bukkit event boilerplate
- Custom events that can use Java's `record` classes
- Cancellable custom events that eliminate 100% of Bukkit cancellable event boilerplate
- Core listeners that can listen to custom events with a familiar Bukkit-like format
- Core listeners that can be registered/unregistered without needing plugin instance

### Lang Provider
(Completely reworked in v0.0.17!)

Easily centralize your plugin's messaging, enabling you to allow end users to configure
messages (e.g. through a config file like `lang.yml`) while maintaining simple in-code access.

Features:
- Create, get, and set lang messages using `LangKey`
- Lang messages support custom placeholders in Java (e.g. `%s`, `%d`) or ElvenideCore (`{}`) formats
- Several built-in lang messages in `Core.lang`, allowing you to customize various ElvenideCore messages
- Several built-in lang messages in `Core.lang` for common plugin uses
- Import lang messages from a config into an enum using `Core.lang.fromConfig()`

### Text Provider
Provides a simple way to convert between Strings and MiniMessage components, and to utilize both.

Features:
- Convert between Strings and MiniMessage Components using `Core.text.from()` and `Core.text.toString()`
- Convert MiniMessage Components to legacy ChatColor-coded Strings using `Core.text.toLegacyString()`
- Remove MiniMessage tags from Strings/Components using `Core.text.stripTags()` or `Core.text.toPlainString()`
- Directly send messages to audiences using `Core.text.send()`
  - Use of `Audience` allows you to send messages to a single player, group of players, or an entire server at once
  - Supports Java and ElvenideCore placeholders
  - Message and placeholders are Objects, not Strings
    - Allows sending ints, doubles, booleans, and more without explicit type casting
    - Allows sending `LangKey` instances (see [Lang Provider](#lang-provider))
- Directly send titles to audiences using `Core.text.sendTitle()`
- Directly send action bars to audiences using `Core.text.sendActionBar()`
- Supports all standard MiniMessage tags
- Custom `<escape>` tag to escape MiniMessage tags
- Various built-in custom color tag packages that can optionally be registered via `Core.text.packages`
- Register your own custom color tags using `Core.text.addColorTag()`
- Convert strings to title case using `Core.text.toTitleCase()`
- Text formatter that supports both Java (e.g. `%s`, `%d`) and ElvenideCore (`{}`) placeholders
  - ElvenideCore placeholders support the same form `{}` for all datatypes
  - Example: `Core.text.format("Hello, {} {}!", "world", 5)` -> `Hello, world 5!`

### Log Provider
Send various component-enabled logs to the console with low verbosity.

Features:
- Send info, warning, error, and debug logs using `Core.log.info()`, `Core.log.warn()`, `Core.log.err()`, and `Core.log.debug()`
  - MiniMessage and custom ElvenideCore tags are supported
  - Java format placeholders (e.g. `%s`, `%d`) and ElvenideCore placeholders (e.g. `{}`) are supported
  - Uses Objects instead of Strings to allow using non-String types without explicit type casting
- Optionally attach throwable errors to error logs
- Assert conditions and automatically log the result using `Core.log.asserts()`
- Assert value equality and automatically log the result using `Core.log.assertEqual()`
- Enable/disable debug logging and assertion logging using `Core.log.setDebugModeEnabled()`
- Enable/disable logging entirely using `Core.log.setLoggerEnabled()`

### Task Provider
Provides a builder to easily create and schedule tasks without needing a plugin instance.

Features:
- Build tasks using `Core.tasks.builder()`, which returns a new `Task` instance
- Define any number of task handlers using `Task#then()`
- Schedule tasks to run delayed, repeated, or on the next tick
  - Default form runs synchronously with a time unit of ticks (20 ticks = 1 second)
  - Alternative forms that run asynchronously
  - Alternative forms that use a time unit of seconds
- Directly cancel tasks using `Task#cancel()`
- Check if tasks have been cancelled using `Task#isCancelled()`
- Get the number of times a task has been executed using `Task#getExecutions()`
- Get the number of ticks that have passed since the task was last executed using `Task#getTicksPassed()`
- Easily access these `Task` instance methods within `Task#then()` handlers

### Perm Provider
Provides some useful utilities for permission checking.

Features:
- Check player/console permissions using `Core.perms.has()`
  - Supports normal permissions (e.g. `permission.node`); checks if you have the perm or are op
  - Supports negated permissions (e.g. `-permission.node`); checks if you *don't* have the perm
  - Supports explicit permissions (e.g. `#permission.node`); checks if you explicitly have the perm, ignoring op
- Experimentally obtain a map of all permissions a player explicitly has using `Core.perms.all()`
  - Not guaranteed to be accurate or up-to-date
  - Get guaranteed accurate information by using a dedicated permission plugin API instead

### Key Provider
Easily manage your plugin's `NamespacedKey`s.

Generate a `NamespacedKey` from a String:
```java
NamespacedKey yourItemKey = Core.keys.get("your.keys.item");
```

Or easily define many keys at once using `enum`s:
```java
public enum YourKeys {
    ITEM, 
    BLOCK,
    HEROBRINE
}

NamespacedKey yourItemKey = Core.keys.get(YourKeys.ITEM);
NamespacedKey yourBlockKey = Core.keys.get(YourKeys.BLOCK);
NamespacedKey yourHeroBrineKey = Core.keys.get(YourKeys.HEROBRINE);
```

Features:
- Generate `NamespacedKey` from a String
- Generate several `NamespacedKey`s from an `enum`
  - The generated key string is dependent on the name of the enum member and the enum itself
  - For example:
    - Renaming `YourKeys` to `OurKeys` in the above example will change the `ITEM`, `BLOCK`, and `HEROBRINE` keys
    - Renaming `HEROBRINE` to `HEROBRINES` in the above example will change the `HEROBRINE` key
- Use plugin or custom string as namespace
- Also supports creating `GoalKey`s using string and enum keys

### Item Provider
Build, edit, and manipulate complex `ItemStack`s with ease.

Features:
- Build a new `ItemStack` using `Core.items.create(Material)`
- Edit an existing `ItemStack` using `Core.items.edit(ItemStack)`
- Build a new `ItemStack` from a material-with-components string using `Core.items.create(String)`
- Manipulate new and existing `ItemStack`s using `ItemBuilder`
  - Set amount
  - Set name
    - Removes the italic-by-default formatting of modern item names
    - Still supports italic MiniMessage tags
  - Set or add lore
    - Removes the italic-by-default formatting of modern item lore
    - Still supports italic MiniMessage tags
  - Set max stack size
  - Set owner of player head (to set its skin)
  - Set color of leather armor and leather horse armor
  - Set or add enchantments
    - Supports safe and unsafe enchantments
  - Various helper methods to add 1.21+ item components
    - Non-experimental API, but provides limited functionality
  - Directly set 1.21+ item components using Paper Component API
    - Experimental API, but provides full functionality
  - Set persistent data using a String-based or Enum-based namespaced key
  - Directly edit item meta via `ItemBuilder#meta()` for properties not covered by this builder
- Use `Core.items.getData()` to access persistent data added to an item
- Use `Core.items.hasData()` to check if an item has persistent data

### Config Provider
Easily load, reload, manipulate, and save YAML configs without the need for massive config boilerplate code.

Features:
- ElvenideCore's config and config section instances can be a drop-in replacement for your existing config code, with no refactoring required
- Get a `Config` instance using `Core.config.get(String)`
- Get a `Config` instance with a default resource config using `Core.config.get(String, String)`
- Delete a config file using `Core.config.deleteFile()`
- `Config` extends `YamlConfiguration`
  - Supports all methods on Bukkit's `YamlConfiguration`
  - Additional features include:
    - Directly reload a config file using `Config#reload()`
    - Directly save a config file using `Config#save()`
    - Section-based methods such as `createSection()` now return a `ConfigSection`
- New `ConfigSection` extending `ConfigurationSection`
  - Supports all methods on Bukkit's `ConfigurationSection`, such as loading and setting ints/doubles/booleans/strings
  - Loads the following additional types:
    - `float`
    - `URI`
    - `Sound`
    - `PotionEffectType`
    - `Material` (modern and legacy)
    - `EntityType`
    - `Particle`
    - `Color` (from hex/rgb format)
    - `Component` (using ElvenideCore's text provider)
- `registerSuppliers(ConfigSupplier...)` method to register Core configs
  - Implement the `ConfigSupplier` interface and register it with the config provider
  - Each supplier can contain any number of Core configs
  - Registered suppliers, and all of their configs, can all be reloaded at once using `reloadSuppliers()`

### Command Provider
Easily create powerful commands that take advantage of Minecraft's brigadier command API, while avoiding the redundancy 
and non-object-oriented approach of the brigadier API.

In-depth documentation is a work in progress.
For now, simply view the in-code documentation on `SubCommand` and `Core.commands.create(String)` and `Core.commands.register()`.

### CoreMenu
Create and manage GUI menus with less boilerplate and no need for slot/index/pagination math.

Features:
- Create feature-complete menus by extending `CoreMenu`
- Define the menu's title and size
- Supports both top (chest) and bottom (player) inventories in a single menu
  - Optionally override `CoreMenu#shouldUseBottomInv()` to disable bottom inventory
- Control both the top and bottom inventories of the menu using `CoreMenu#top` and `CoreMenu#bottom`
- Run code when the menu is opened, closed, or refreshed
- Methods to open, close, and refresh the menu
- Automatically register and unregister all Bukkit listeners needed for the menu
- Built-in support for pagination
- Top and bottom inventories independently paginated
- Easily check if a slot is within a range or region of slot numbers
- Easily fetch an index from a slot number, based on pagination and slot range/region
- Easily populate ranges and regions of slots with a paginated list of items
- Easily assign a single item to ranges, regions, and borders of slots
- Easily create next-page buttons that automatically prevent exceeding last page
- Easily create previous-page buttons that automatically prevent exceeding first page
- Automatically saves players' inventories before opening menus and restores after closing
- Directly assign click handlers to any item, range of items, or region of items

### CoreMap (new in v0.0.17)
Powerful hashmap utility with chainable methods.

Features:
- Extends `LinkedHashMap`
  - Allows use as a drop-in replacement for `Map`, `HashMap`, or `LinkedHashMap`
  - Allows key-based insertion, modification, and removal (like a `HashMap`)
  - Allows maintenance of and iteration in insertion order (like a `LinkedList`)
- Chainable methods for easily adding and updating entries in the map
- Clone the map using `clone()`

## Further Documentation
Most features are directly documented via JavaDoc in the code 
(obtainable in IntelliJ IDEA via `Maven > Download Sources and/or Documentation`).
You can also view the ElvenideCore source code on this repository to help understand how various methods work.
