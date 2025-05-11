package com.elvenide.core.providers.command;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CommandBuilder {

    private final String name;
    private LiteralArgumentBuilder<CommandSourceStack> root;
    NodeWrapper commandNode;
    String[] aliases = {};
    String description = "Command created with the ElvenideCore library.";

    CommandBuilder(String name) {
        this.name = name;
        this.root = Commands.literal(name);
        this.root.executes(this::helpExecutor);
        this.commandNode = new SubGroupBuilder(name, null).buildHashed();
        CommandRegistry.commands.add(this);
    }

    /* <editor-fold defaultstate="collapsed" desc="Command Building"> */

    /**
     * Sets the description of the command.
     * @param description Description
     * @return This
     */
    @PublicAPI
    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the aliases of the command.
     * @param aliases Aliases
     * @return This
     */
    @PublicAPI
    public CommandBuilder setAliases(String... aliases) {
        this.aliases = aliases;
        return this;
    }

    /**
     * By default, an ElvenideCore command does not directly have arguments or execution,
     * and instead uses subcommands with such:<br />
     * <code>/command subGroupOrCommand &lt;arguments&gt;</code>
     * <p>
     * Using this method will give this command its own arguments and execution:<br />
     * <code>/command &lt;arguments&gt;</code>
     * <p>
     * Any other subgroups and subcommands will be removed. The label of the
     * provided main command will be overridden by the command's name.
     * @param subCommand A SubCommand instance to set as the main command
     */
    @PublicAPI
    public void setMainCommand(@NotNull SubCommand subCommand) {
        NodeWrapper wrapper = new NodeWrapper(new SubCommand() {
            @Override
            public @NotNull String label() {
                return CommandBuilder.this.name;
            }

            @Override
            public void setup(@NotNull SubCommandBuilder builder) {
                subCommand.setup(builder);
                if (builder.description == null)
                    builder.setDescription(CommandBuilder.this.description);
            }

            @Override
            public void executes(@NotNull SubCommandContext context) {
                subCommand.executes(context);
            }
        }, null);
        this.root = getNodeTree(wrapper);
        this.commandNode = wrapper;
    }

    /**
     * Adds a subgroup to the command. Subgroups can contain subcommands or other subgroups.
     * @param name Name of the subgroup
     * @param builder Builder function to configure the subgroup
     * @return This
     */
    @PublicAPI
    public CommandBuilder addSubGroup(String name, Consumer<SubGroupBuilder> builder) {
        SubGroupBuilder groupBuilder = new SubGroupBuilder(name, commandNode);
        builder.accept(groupBuilder);
        root.then(getNodeTree(groupBuilder.buildHashed()));
        commandNode.asSubGroup().addSubNode(groupBuilder.buildHashed());
        return this;
    }

    /**
     * Adds a subcommand to the command. Subcommands can have arguments and execution.
     * @param subCommand Name of the subcommand
     * @return This
     */
    @PublicAPI
    public CommandBuilder addSubCommand(@NotNull SubCommand subCommand) {
        NodeWrapper wrapper = new NodeWrapper(subCommand, commandNode);
        root.then(getNodeTree(wrapper));
        commandNode.asSubGroup().addSubNode(wrapper);
        return this;
    }

    /**
     * Adds a built-in "help" subcommand to the command, which can display auto-generated usage
     * information for all subgroups and subcommands associated with this command.
     * <p>
     * As of v0.0.15, the help subcommand now accepts a command name as an optional argument.
     * @return This
     */
    @PublicAPI
    public CommandBuilder addHelpSubCommand() {
        return addSubCommand(new BuiltinHelpCommand(this));
    }

    LiteralCommandNode<CommandSourceStack> build() {
        return root.build();
    }

    /* </editor-fold> */

    /* <editor-fold defaultstate="collapsed" desc="Command Execution Functions"> */

    private int validatedExecutor(NodeWrapper commandWrapper, SubCommandContext ctx) {
        if (ctx.subCommandData.playerOnly && !ctx.isPlayer()) {
            ctx.reply(Core.lang.common.NOT_PLAYER);
            return Command.SINGLE_SUCCESS;
        }

        if (ctx.subCommandData.permission != null && !ctx.hasPermission(ctx.subCommandData.permission)) {
            ctx.reply(Core.lang.common.NO_PERMISSION);
            return Command.SINGLE_SUCCESS;
        }

        try {
            commandWrapper.asSubCommand().executes(ctx);
        }
        catch (InvalidArgumentException e) {
            ctx.replyToSender("<hover:show_text:\"%s\">%s</hover>",
                commandWrapper.generateUsage(ctx.executor()),
                "<red>" + e.getMessage()
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private int helpExecutor(CommandContext<CommandSourceStack> rawCtx) {
        return helpExecutor(rawCtx, commandNode);
    }

    private int helpExecutor(CommandContext<CommandSourceStack> rawCtx, @NotNull NodeWrapper wrapper) {
        SubCommandContext ctx = new SubCommandContext(rawCtx, new SubCommandBuilder(), this, 0);

        // Send command header
        ctx.reply(" ");
        ctx.reply(Core.lang.common.COMMAND_HEADER);

        // Send description
        if (wrapper.isSubCommand()) {
            wrapper.asSubCommand().setup(ctx.subCommandData);
            if (ctx.subCommandData.description != null)
                ctx.reply(ctx.subCommandData.description);
            else
                ctx.reply(description);
        }
        else if (wrapper == commandNode) {
            ctx.reply(description);
        }

        // Send usage
        ctx.reply(wrapper.generateUsage(ctx.executor()));
        return Command.SINGLE_SUCCESS;
    }

    /* </editor-fold> */

    /* <editor-fold defaultstate="collapsed" desc="Command Tree Generation"> */

    private RequiredArgumentBuilder<CommandSourceStack, ?> getArgumentTree(LinkedList<SubArgumentBuilder> remainingArgs, NodeWrapper wrapper, SubCommandBuilder argBuilder, int specifiedArgs) {
        // Pop subargument from remaining list
        final SubArgumentBuilder arg = remainingArgs.pop();

        // Create brigadier argument from subargument
        RequiredArgumentBuilder<CommandSourceStack, ?> argNode = Commands
                .argument(arg.label, arg.type)
                .executes(context -> validatedExecutor(wrapper, new SubCommandContext(context, argBuilder, this, specifiedArgs)));

        // Add argument suggestions, if any
        if (arg.suggester != null)
            argNode.suggests((ctx, builder) -> {
                SubCommandContext context = new SubCommandContext(ctx, argBuilder, this, 0);

                String openBracket = arg.required ? "<" : "[";
                String closeBracket = arg.required ? ">" : "]";
                arg.suggester
                        .apply(context)
                        .stream()
                        .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                        .forEach(entry -> builder.suggest(entry, MessageComponentSerializer.message().serialize(
                                Core.text.deserialize("<aqua>\\" + openBracket + arg.label + closeBracket + "</aqua>")
                        )));
                return builder.buildFuture();
            });

        // If there are more arguments to add, add them recursively
        if (!remainingArgs.isEmpty())
            argNode.then(getArgumentTree(remainingArgs, wrapper, argBuilder, specifiedArgs + 1));

        return argNode;
    }

    private LiteralArgumentBuilder<CommandSourceStack> getNodeTree(@NotNull NodeWrapper wrapper) {
        // Create subcommand literal
        LiteralArgumentBuilder<CommandSourceStack> current = Commands.literal(wrapper.asSubNode().label());

        // If it is a subgroup, add its child nodes to current node
        if (wrapper.isSubGroup()) {
            for (NodeWrapper subWrapper : wrapper.asSubGroup().getChildNodes()) {
                LiteralArgumentBuilder<CommandSourceStack> child = getNodeTree(subWrapper);
                current.then(child);
            }

            // Add help executor to current subgroup node
            current.executes(context -> helpExecutor(context, wrapper));
        }

        // Otherwise if it is a subcommand, add its arguments to current node
        else if (wrapper.isSubCommand()) {
            // Create args
            SubCommandBuilder argBuilder = new SubCommandBuilder();
            wrapper.asSubCommand().setup(argBuilder);

            // If there are any args
            if (!argBuilder.subArgs.isEmpty()) {
                // Add args with executor to current node
                current.then(getArgumentTree(new LinkedList<>(argBuilder.subArgs), wrapper, argBuilder, 1));
                assert argBuilder.subArgs.peekFirst() != null;

                // If first arg is required, add help executor to current subcommand node
                if (argBuilder.subArgs.peekFirst().required)
                    current.executes(context -> helpExecutor(context, wrapper));

                // Otherwise if first arg is optional, add validated executor on current subcommand node
                else
                    current.executes(context -> validatedExecutor(wrapper, new SubCommandContext(context, argBuilder, this, 0)));
            }

            // Otherwise, add validated executor on current leaf node
            else
                current.executes(context -> validatedExecutor(wrapper, new SubCommandContext(context, argBuilder, this, 0)));
        }

        return current;
    }

    /* </editor-fold> */

}
