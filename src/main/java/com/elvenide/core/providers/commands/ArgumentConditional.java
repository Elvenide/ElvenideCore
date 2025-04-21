package com.elvenide.core.providers.commands;

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
    public ArgumentConditional andIfEqual(@NotNull String argName, @NotNull String value) {
        return andIfTrue(args.isEqual(argName, value));
    }

    /**
     * Modifies the condition such that the previous condition must be true AND the new condition must be true.
     * @param condition New condition
     * @return This
     * @since 0.0.9
     */
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
    public ArgumentThen then(Runnable runnable) {
        return new ArgumentThen(this).then(runnable);
    }

    public static class ArgumentThen {

        private final ArgumentConditional conditional;
        private ArgumentThen(ArgumentConditional conditional) {
            this.conditional = conditional;
        }

        /**
         * Code that will only execute if the condition is true.
         * @param runnable Runnable
         * @return This
         * @since 0.0.6
         */
        public ArgumentThen then(Runnable runnable) {
            if (conditional.operational && conditional.isTrue)
                runnable.run();
            return this;
        }

        /**
         * Code that will only execute if the condition is false.
         * @param runnable Runnable
         * @return This
         * @since 0.0.7
         */
        public ArgumentThen orElse(Runnable runnable) {
            if (conditional.operational && !conditional.isTrue)
                runnable.run();

            return this;
        }

        /**
         * If the condition is false, throws an error and sends the error message to the sender.
         * @param errorMessage Error message
         * @param shouldDisplayUsage Whether to send command usage info to the sender
         * @return This
         * @since 0.0.7
         * @deprecated Use {@link #orEnd(String)} instead. The <code>shouldDisplayUsage</code>
         * parameter is internally always handled as <code>true</code>.
         */
        @Deprecated(forRemoval = true, since = "0.0.14")
        public ArgumentThen orEnd(String errorMessage, boolean shouldDisplayUsage) throws InvalidArgumentException {
            if (conditional.operational && !conditional.isTrue)
                throw new InvalidArgumentException("%s", errorMessage);

            return this;
        }

        /**
         * If the condition is false, throws an error and sends the error message to the sender.
         * @param errorMessage Error message
         * @return This
         * @since 0.0.14
         */
        public ArgumentThen orEnd(String errorMessage) throws InvalidArgumentException {
            if (conditional.operational && !conditional.isTrue)
                throw new InvalidArgumentException("%s", errorMessage);

            return this;
        }

        /**
         * Creates a new conditional that will only execute if the previous condition was false
         * and the new argument was provided.
         * @param argName Name of the new argument
         * @return This
         * @since 0.0.8
         */
        public ArgumentConditional orIfProvided(String argName) {
            return orIfTrue(conditional.args.isProvided(argName));
        }

        /**
         * Creates a new conditional that will only execute if the previous condition was false
         * and the new argument has a specific value.
         * @param argName Name of the new argument
         * @param value Expected value
         * @return This
         * @since 0.0.8
         */
        public ArgumentConditional orIfEqual(String argName, String value) {
            return orIfTrue(conditional.args.isEqual(argName, value));
        }

        /**
         * Creates a new conditional that will only execute if the previous condition was false
         * and the new condition is true.
         * @param condition New condition
         * @return This
         * @since 0.0.9
         */
        public ArgumentConditional orIfTrue(boolean condition) {
            if (conditional.operational && !conditional.isTrue)
                return new ArgumentConditional(conditional.args, condition, true);

            return new ArgumentConditional(conditional.args, false, false);
        }

    }

}
