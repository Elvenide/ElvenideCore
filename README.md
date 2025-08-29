# ElvenideCore
ElvenideCore is a powerful library for PaperMC plugins, primarily used in many of my own plugins.
It is not a standalone plugin, and must be shaded into your own plugin.

## DEV ONLY VERSION

This is a mostly stable, but untested, dev build opened up for public and private use. This branch will not receive additional updates or bug fixes, and you should instead use the official v0.0.17 release when it is published. This branch is almost certain to have bugs due to its untested nature.

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
    <version>ver/0.0.17-dev-4-SNAPSHOT</version>
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
The documentation in the README file at the `dev/0.0.17` branch should cover most features on this branch, although it is not necessarily 1:1.

Most features are directly documented via JavaDoc in the code 
(obtainable in IntelliJ IDEA via `Maven > Download Sources and/or Documentation`).
You can also view the ElvenideCore source code on this repository to help understand how various methods work.
