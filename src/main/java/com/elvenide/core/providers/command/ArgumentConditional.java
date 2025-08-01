package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ArgumentConditional {

    private final SubArgumentContext args;
    private boolean isTrue;
    private final boolean operational;

    ArgumentConditional(SubArgumentContext args, boolean condition, boolean operational) {
        isTrue = condition;
        this.args = args;
        this.operational = operational;
    }

    /**
     * Modifies the condition such that the previous condition must be true AND the new argument must be provided.
     * @param argName Name of the new argument
     * @return This
     * @since 0.0.8
     */
    @PublicAPI
    @Contract(pure = true)
    public ArgumentConditional andIfProvided(@NotNull String argName) {
        return andIfTrue(args.isProvided(argName));
    }

    /**
     * Modifies the condition such that the previous condition must be true AND the new argument must have a specific value.
     * @param argName Name of the new argument
     * @param value Expected value
     * @return This
     * @since 0.0.8
     */
    @PublicAPI
    @Contract(pure = true)
    public <T> ArgumentConditional andIfEqual(@NotNull String argName, @NotNull T value) {
        return andIfTrue(args.isEqual(argName, value));
    }

    /**
     * Modifies the condition such that the previous condition must be true AND the new condition must be true.
     * @param condition New condition
     * @return This
     * @since 0.0.9
     */
    @PublicAPI
    @Contract(pure = true)
    public ArgumentConditional andIfTrue(boolean condition) {
        if (operational)
            isTrue = isTrue && condition;
        return this;
    }

    /**
     * Code that will only execute if the condition is true.
     * @param runnable Runnable
     * @return This
     * @since 0.0.6
     */
    @PublicAPI
    public ArgumentConditional then(Runnable runnable) {
        if (operational && isTrue)
            runnable.run();
        return this;
    }

    /**
     * If the condition is true, throws an error and sends the error message to the sender.
     * @param errorMessage Error message
     * @param placeholders Optional placeholders
     * @return This
     * @throws InvalidArgumentException If the condition is true
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional thenEnd(Object errorMessage, Object... placeholders) throws InvalidArgumentException {
        if (operational && isTrue)
            throw new InvalidArgumentException("%s", Core.text.format(errorMessage, placeholders));

        return this;
    }

    /**
     * If the condition is true, sends the message to the command executor or sender.
     * @param message String message
     * @param placeholders Optional placeholders
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional thenReply(Object message, Object... placeholders) {
        if (operational && isTrue)
            args.sub.reply(message, placeholders);
        return this;
    }

    /**
     * If the condition is true, sends the message to the command sender.
     * @param message String message
     * @param placeholders Optional placeholders
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional thenReplyToSender(Object message, Object... placeholders) {
        if (operational && isTrue)
            args.sub.replyToSender(message, placeholders);
        return this;
    }

    /**
     * If the condition is true, sends the message to the target audience.
     * @param target The audience
     * @param message String message
     * @param placeholders Optional placeholders
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional thenSend(Audience target, Object message, Object... placeholders) {
        if (operational && isTrue)
            Core.text.send(target, message, placeholders);
        return this;
    }

    /**
     * Code that will only execute if the condition is false.
     * @param runnable Runnable
     * @return This
     * @since 0.0.7
     */
    @PublicAPI
    public ArgumentConditional orElse(Runnable runnable) {
        if (operational && !isTrue)
            runnable.run();

        return this;
    }

    /**
     * If the condition is false, throws an error and sends the error message to the sender.
     * @param errorMessage Error message
     * @param placeholders Optional placeholders for error message
     * @return This
     * @throws InvalidArgumentException If the condition is false
     * @since 0.0.14
     */
    @PublicAPI
    public ArgumentConditional orEnd(Object errorMessage, Object... placeholders) throws InvalidArgumentException {
        if (operational && !isTrue)
            throw new InvalidArgumentException("%s", Core.text.format(errorMessage, placeholders));

        return this;
    }

    /**
     * If the condition is false, sends the message to the command executor or sender.
     * @param message String message
     * @param placeholders Optional placeholders
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional orReply(Object message, Object... placeholders) {
        if (operational && !isTrue)
            args.sub.reply(message, placeholders);
        return this;
    }

    /**
     * If the condition is false, sends the message to the command sender.
     * @param message String message
     * @param placeholders Optional placeholders
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional orReplyToSender(Object message, Object... placeholders) {
        if (operational && !isTrue)
            args.sub.replyToSender(message, placeholders);
        return this;
    }

    /**
     * If the condition is false, sends the message to the target audience.
     * @param target The audience
     * @param message String message
     * @param placeholders Optional placeholders
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ArgumentConditional orSend(Audience target, Object message, Object... placeholders) {
        if (operational && !isTrue)
            Core.text.send(target, message, placeholders);
        return this;
    }

    /**
     * Creates a new conditional that will only execute if the previous condition was false
     * and the new argument was provided.
     * @param argName Name of the new argument
     * @return This
     * @since 0.0.8
     */
    @PublicAPI
    @Contract(pure = true)
    public ArgumentConditional orIfProvided(@NotNull String argName) {
        return orIfTrue(args.isProvided(argName));
    }

    /**
     * Creates a new conditional that will only execute if the previous condition was false
     * and the new argument has a specific value.
     * @param argName Name of the new argument
     * @param value Expected value
     * @return This
     * @since 0.0.8
     */
    @PublicAPI
    @Contract(pure = true)
    public <T> ArgumentConditional orIfEqual(@NotNull String argName, @NotNull T value) {
        return orIfTrue(args.isEqual(argName, value));
    }

    /**
     * Creates a new conditional that will only execute if the previous condition was false
     * and the new condition is true.
     * @param condition New condition
     * @return This
     * @since 0.0.9
     */
    @PublicAPI
    @Contract(pure = true)
    public ArgumentConditional orIfTrue(boolean condition) {
        if (operational && !isTrue)
            return new ArgumentConditional(args, condition, true);

        return new ArgumentConditional(args, false, false);
    }

}
