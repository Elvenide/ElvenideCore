package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

class BuiltinHelpCommand implements SubCommand {
    private final NodeWrapper commandNode;
    private final String commandDescription;

    BuiltinHelpCommand(CommandBuilder builder) {
        this.commandNode = builder.commandNode;
        this.commandDescription = builder.description;
    }

    @Override
    public @NotNull String label() {
        return "help";
    }

    @Override
    public void setup(@NotNull SubCommandBuilder builder) {
        builder
            .addGreedy("command", argument ->
                argument
                    .setOptional()
                    .suggests(ctx -> commandNode.getSubPaths())
            );
    }

    @Override
    public void executes(@NotNull SubCommandContext ctx) {
        AtomicReference<NodeWrapper> node = new AtomicReference<>(null);

        // Get the node, depending on if args are provided
        ctx.args
            .ifProvided("command")
            .then(() -> {
                String path = ctx.args.getString("command");
                node.set(commandNode.findDescendant(path));
                ctx.args
                    .ifTrue(node.get() == null)
                    .thenEnd("There is no such command: {}", path);
            })
            .orElse(() -> {
                node.set(commandNode.getNodeWrapper(this));
            });

        // Get the final node to show usage for
        @NotNull NodeWrapper wrapper = node.get();

        // Send command header
        ctx.reply(" ");
        ctx.reply(Core.lang.common.COMMAND_HEADER);

        // Send description
        if (wrapper.isSubCommand()) {
            wrapper.asSubCommand().setup(ctx.subCommandData);
            if (ctx.subCommandData.description != null)
                ctx.reply(ctx.subCommandData.description);
            else
                ctx.reply(commandDescription);
        }
        else if (wrapper == commandNode) {
            ctx.reply(commandDescription);
        }

        // Send usage
        ctx.reply(wrapper.generateUsage(ctx.executor()));
    }
}
