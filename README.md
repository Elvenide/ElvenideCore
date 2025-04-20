# ElvenideCore
ElvenideCore is a powerful library for PaperMC plugins, primarily used in many of my own plugins.
It is not a standalone plugin, and must be shaded into your own plugin.

## Versions
- [`0.0.13`](https://github.com/Elvenide/ElvenideCore/releases/tag/0.0.13) for Paper 1.21.4 (latest)

## Get Started

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
    <version>0.0.13</version>
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
            </configuration>
        </plugin>
    </plugins>
</build>
```

You can now start using ElvenideCore. To do so, access the static properties of the `ElvenideCore` class.

```java
import com.elvenide.core.ElvenideCore;

public class YourPlugin extends JavaPlugin {
    public void onEnable() {
        // Install more colors into the ElvenideCore MiniMessage instance
        ElvenideCore.text.packages.moreColors.install();
    }
}
```

The following features are available for you to mess around with:
- `ElvenideCore.text` - Convert between Strings and MiniMessage components. Supports all standard MiniMessage tags, some new ElvenideCore tags (`<elang>`, `<escape>`), and your own custom color tags.
- `ElvenideCore.config` - Easily load, reload, and manipulate YAML configs.
- `ElvenideCore.lang` - Define your plugin's messaging in a central location (e.g. through a `lang.yml`), then use those messages in any MiniMessage text using `<elang>`.
- `ElvenideCore.perms` - Some useful utilities for permission checking.
- `ElvenideCore.commands` - Easily create powerful commands that take advantage of Minecraft's brigadier command API, while avoiding the redundancy and non-object-oriented approach of the brigadier API.

More in-depth documentation will be provided in the future. Most features are documented via JavaDoc in the code (obtainable in IntelliJ IDEA via `Maven > Download Sources and/or Documentation`).
