# ElvenideCore
ElvenideCore is a powerful library for PaperMC plugins, primarily used in many of my own plugins.
It is not a standalone plugin, and must be shaded into your own plugin.

## Versions
- [v0.0.15](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.15) for Paper 1.21.8 (latest)
- [v0.0.14](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.14) for Paper 1.21.4
- [v0.0.13](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.13) for Paper 1.21.4

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
    <groupId>com.github.Elvenide</groupId>
    <artifactId>ElvenideCore</artifactId>
    <version>0.0.15</version>
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

## Getting Started
You can now start using ElvenideCore.

### CorePlugin (v0.0.15+)
Initialize your plugin by extending the `CorePlugin` class instead of `JavaPlugin`.
`CorePlugin` extends `JavaPlugin` and provides many useful utility methods.

The use of `CorePlugin` is optional, but extra initialization may be necessary to use certain ElvenideCore features without it.

```java
import com.elvenide.core.providers.plugin.CorePlugin;

public class YourPlugin extends CorePlugin {
    @Override
    public void onEnabled() {
        // Plugin start up functionality
    }

    @Override
    public void onDisabled() {
        // Plugin shut down functionality
    }
}
```

Features:
- Extends `JavaPlugin`, and can be used wherever `JavaPlugin` can be used
- Static methods to register/unregister Bukkit listeners without needing plugin instance
- Static `registerConfigSuppliers(ConfigSupplier...)` method to register Core configs
  - Registered Core configs will automatically be loaded right after the plugin is enabled
  - Registered Core configs can all be reloaded at once using static `reload()` method

### CoreEvent (v0.0.15+)
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
Easily centralize your plugin's messaging, enabling you to allow end users to configure
messages (e.g. through a config file like `lang.yml`) while maintaining simple in-code access.

Features:
- Get and set lang messages using `Core.lang`
- Access lang messages using `<elang>` tags in any MiniMessage-compatible features of ElvenideCore
- Lang messages support custom placeholders in Java (e.g. `%s`, `%d`) or ElvenideCore (`{}`) formats
- Several built-in lang messages in `Core.lang.common`, allowing you to customize various ElvenideCore messages
- `LangKeySupplier` that can return `LangKey` objects directly usable in the Text Provider 

### Text Provider
Provides a simple way to convert between Strings and MiniMessage components, and to utilize both.

Features:
- Convert between Strings and MiniMessage components using `Core.text.deserialize()` and `Core.text.serialize()`
- Directly send messages to audiences using `Core.text.send()`
  - Use of `Audience` allows you to send messages to a single player, group of players, or an entire server at once
  - Supports Java and ElvenideCore placeholders
  - Message and placeholders are Objects, not Strings
    - Allows sending ints, doubles, booleans, and more without explicit type casting
    - Allows sending `LangKey` instances (see [Lang Provider](#lang-provider))
- Supports all standard MiniMessage tags
- Custom `<elang>` tag (see [Lang Provider](#lang-provider))
- Custom `<escape>` tag to escape MiniMessage tags
- Various built-in custom color tag packages that can optionally be registered via `Core.text.packages`
- Register your own custom color tags using `Core.text.addColorTag()`
- Text formatter that supports both Java (e.g. `%s`, `%d`) and ElvenideCore (`{}`) placeholders
  - ElvenideCore placeholders support the same form `{}` for all datatypes
  - Example: `Core.text.format("Hello, {} {}!", "world", 5)` -> `Hello, world 5!`

### Log Provider (v0.0.15+)
Send various component-enabled logs to the console with low verbosity.

Features:
- Send info, warning, error, and debug logs using `Core.log.info()`, `Core.log.warn()`, `Core.log.err()`, and `Core.log.debug()`
  - MiniMessage and custom ElvenideCore tags are supported
  - Java format placeholders (e.g. `%s`, `%d`) and ElvenideCore placeholders (e.g. `{}`) are supported
  - Uses Objects instead of Strings to allow using non-String types without explicit type casting
- Assert conditions and automatically log the result using `Core.log.asserts()`
- Assert value equality and automatically log the result using `Core.log.assertEqual()`
- Enable/disable debug logging and assertion logging using `Core.log.setDebugModeEnabled()`
- Enable/disable logging entirely using `Core.log.setLoggerEnabled()`

### Task Provider (v0.0.15+)
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

### Key Provider (v0.0.14+)
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

### Item Provider (v0.0.15+)
Build, edit, and manipulate complex `ItemStack`s with ease.

Features:
- Build a new `ItemStack` using `Core.items.builder(Material)`
- Edit an existing `ItemStack` using `Core.items.builder(ItemStack)`
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
  - Set 1.21+ item components
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
- Easily reload multiple configs by registering a `ConfigSupplier` and using `Core.reload()` (see [Core Plugin](#coreplugin-v0015) section)

### Command Provider
Easily create powerful commands that take advantage of Minecraft's brigadier command API, while avoiding the redundancy 
and non-object-oriented approach of the brigadier API.

In-depth documentation is a work in progress.
For now, simply view the in-code documentation on `SubCommand` and `Core.commands.create(String)` and `Core.commands.register()`.

### CoreMenu (v0.0.15+)
Create and manage GUI menus with less boilerplate and no need for slot/index/pagination math.

Features:
- Create feature-complete menus by extending `CoreMenu`
- Define the menu's title and size
- Supports both top (chest) and bottom (player) inventories in a single menu
- Control both the top and bottom inventories of the menu using `CoreMenu#top` and `CoreMenu#bottom`
- Run code when the menu is opened, closed, or refreshed
- Methods to open, close, and refresh the menu
- Automatically register and unregister all Bukkit listeners needed for the menu
- Built-in support for pagination
- Top and bottom inventories independently paginated
- Easily check if a slot is within a range or region of slot numbers
- Easily fetch an index from a slot number, based on pagination and slot range/region
- Easily populate ranges and regions of slots with a paginated list of items
- Easily populate a rectangular region's border slots with an item
- Easily create next-page buttons that automatically prevent exceeding last page
- Easily create previous-page buttons that automatically prevent exceeding first page
- Automatically saves players' inventories before opening menus and restores after closing
- Directly assign click handlers to any item, range of items, or region of items

## Further Documentation
Most features are directly documented via JavaDoc in the code 
(obtainable in IntelliJ IDEA via `Maven > Download Sources and/or Documentation`).
You can also view the ElvenideCore source code on this repository to help understand how various methods work.