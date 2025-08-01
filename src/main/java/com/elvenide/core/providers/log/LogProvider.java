package com.elvenide.core.providers.log;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.api.PublicAPI;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LogProvider extends Provider {

    private boolean enableLogger = true;
    private boolean enableDebug = true;

    @ApiStatus.Internal
    public LogProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Sets the flag that handles whether logging is enabled.
     * @param value Boolean value
     * @since 0.0.15
     */
    @PublicAPI
    public void setLoggerEnabled(boolean value) {
        this.enableLogger = value;
    }

    /**
     * Sets the flag that handles whether debug mode is enabled.
     * @param value Boolean value
     * @since 0.0.15
     */
    @PublicAPI
    public void setDebugModeEnabled(boolean value) {
        this.enableDebug = value;
    }

    /**
     * Logs an info message to console, if logging is enabled (enabled by default).
     * Supports a limited subset of MiniMessage and custom ElvenideCore tags.
     * <p>
     * Placeholders can be inserted using Java format placeholders (e.g. %s, %d) or custom placeholders (e.g. {}).
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public void info(Object text, Object... optionalPlaceholders) {
        if (enableLogger)
            core.plugin.getComponentLogger().info(Core.text.deserialize(text, optionalPlaceholders));
    }

    /**
     * Logs a warning message to console, if logging is enabled (enabled by default).
     * Supports a limited subset of MiniMessage and custom ElvenideCore tags.
     * <p>
     * Placeholders can be inserted using Java format placeholders (e.g. %s, %d) or custom placeholders (e.g. {}).
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public void warn(Object text, Object... optionalPlaceholders) {
        if (enableLogger)
            core.plugin.getComponentLogger().warn(Core.text.deserialize(text, optionalPlaceholders));
    }

    /**
     * Logs an error message to console, if logging is enabled (enabled by default).
     * Supports a limited subset of MiniMessage and custom ElvenideCore tags.
     * <p>
     * Placeholders can be inserted using Java format placeholders (e.g. %s, %d) or custom placeholders (e.g. {}).
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public void err(Object text, Object... optionalPlaceholders) {
        if (enableLogger)
            core.plugin.getComponentLogger().error(Core.text.deserialize(text, optionalPlaceholders));
    }

    /**
     * Logs a debug message to console, if logging is enabled and debug mode is enabled (both enabled by default).
     * Supports a limited subset of MiniMessage and custom ElvenideCore tags.
     * <p>
     * Placeholders can be inserted using Java format placeholders (e.g. %s, %d) or custom placeholders (e.g. {}).
     * @param text String text
     * @param optionalPlaceholders Optional placeholders
     * @since 0.0.15
     */
    @PublicAPI
    public void debug(Object text, Object... optionalPlaceholders) {
        if (enableDebug && enableLogger)
            core.plugin.getComponentLogger().debug(Core.text.deserialize(text, optionalPlaceholders));
    }

    /**
     * Tests if the asserted condition is true and logs the result,
     * if logging is enabled and debug mode is enabled (both enabled by default).
     * Supports a limited subset of MiniMessage and custom ElvenideCore tags.
     * @param conditionName The name of the condition to test
     * @param condition The condition to assert is true
     * @since 0.0.15
     */
    @PublicAPI
    public void asserts(Object conditionName, boolean condition) {
        if (!enableLogger || !enableDebug)
            return;

        info("<blue>Asserting condition: {}", conditionName);

        if (!condition)
            err("<red>Condition failed: {}", conditionName);
        else
            info("<green>Condition passed: {}", conditionName);
    }

    /**
     * Tests if a value is equal to an expected value and logs the result,
     * if logging is enabled and debug mode is enabled (both enabled by default).
     * Supports a limited subset of MiniMessage and custom ElvenideCore tags.
     * @param name The name of the value to test
     * @param expectedValue The expected value, i.e. the correct answer
     * @param actualValue The actual value, i.e. the answer to test the accuracy of
     * @since 0.0.15
     */
    @PublicAPI
    public void assertEqual(Object name, @Nullable Object expectedValue, @Nullable Object actualValue) {
        if (!enableLogger || !enableDebug)
            return;

        info("<blue>Asserting equality: {} == {}", name, expectedValue);

        if (!Objects.equals(expectedValue, actualValue))
            err("<red>Value '{}' not equal: expected '{}' but got '{}'", name, expectedValue, actualValue);
        else
            info("<green>Value '{}' was equal: expected '{}' and got '{}'", name, expectedValue, actualValue);
    }
}
