package com.elvenide.core.providers.commands;

import com.elvenide.core.ElvenideCore;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandBuilder {

    private final String name;
    private LiteralArgumentBuilder<CommandSourceStack> root;
    private final HashMap<String, String> subNodeUsages = new HashMap<>();
    String[] aliases = {};
    String description = "Command created with the ElvenideCore library.";

    CommandBuilder(String name) {
        this.name = name;
        this.root = Commands.literal(name);
        this.root.executes(this::helpExecutor);
        CommandRegistry.commands.add(this);
    }

    /* <editor-fold defaultstate="collapsed" desc="Command Building"> */

    /**
     * Sets the description of the command.
     * @param description Description
     * @return This
     */
    public CommandBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the aliases of the command.
     * @param aliases Aliases
     * @return This
     */
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
    public void setMainCommand(@NotNull SubCommand subCommand) {
        subNodeUsages.clear();
        HashedSubNodeWrapper wrapper = new HashedSubNodeWrapper(new SubCommand() {
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
        });
        parseUsageTree(wrapper, "/");
        this.root = getNodeTree(wrapper);
    }

    private CommandBuilder addSubNode(@NotNull SubNode subNode) {
        HashedSubNodeWrapper wrapper = new HashedSubNodeWrapper(subNode);
        root.then(getNodeTree(wrapper));
        parseUsageTree(wrapper, "/" + name + " ");
        return this;
    }

    /**
     * Adds a subgroup to the command. Subgroups can contain subcommands or other subgroups.
     * @param name Name of the subgroup
     * @param builder Builder function to configure the subgroup
     * @return This
     */
    public CommandBuilder addSubGroup(String name, Consumer<SubGroupBuilder> builder) {
        SubGroupBuilder groupBuilder = new SubGroupBuilder(name);
        builder.accept(groupBuilder);
        return addSubNode(groupBuilder.build());
    }

    /**
     * Adds a subcommand to the command. Subcommands can have arguments and execution.
     * @param subCommand Name of the subcommand
     * @return This
     */
    public CommandBuilder addSubCommand(@NotNull SubCommand subCommand) {
        return addSubNode(subCommand);
    }

    /**
     * Adds a built-in "help" subcommand to the command, which will display auto-generated usage
     * information for all subcommands associated with this command.
     * @return This
     */
    public CommandBuilder addHelpSubCommand() {
        // Create subcommand literal
        LiteralArgumentBuilder<CommandSourceStack> current = Commands.literal("help");

        // Add help executor to current subgroup node
        current.executes(this::helpExecutor);

        // Add to root
        root.then(current);
        return this;
    }

    LiteralCommandNode<CommandSourceStack> build() {
        return root.build();
    }

    /* </editor-fold> */

    /* <editor-fold defaultstate="collapsed" desc="Command Execution Functions"> */

    private int validatedExecutor(HashedSubNodeWrapper commandWrapper, SubCommandContext ctx) {
        if (ctx.subCommandData.playerOnly && !ctx.isPlayer()) {
            ctx.reply(ElvenideCore.lang.common.NOT_PLAYER);
            return Command.SINGLE_SUCCESS;
        }

        if (ctx.subCommandData.permission != null && !ctx.hasPermission(ctx.subCommandData.permission)) {
            ctx.reply(ElvenideCore.lang.common.NO_PERMISSION);
            return Command.SINGLE_SUCCESS;
        }

        try {
            commandWrapper.asSubCommand().executes(ctx);
        }
        catch (InvalidArgumentException e) {
            ctx.replyToSender("<hover:show_text:\"%s\">%s</hover>",
                ElvenideCore.lang.common.COMMAND_USAGE_PREFIX + getUsage(commandWrapper),
                "<red>" + e.getMessage()
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    private int helpExecutor(CommandContext<CommandSourceStack> rawCtx) {
        return helpExecutor(rawCtx, null);
    }

    private int helpExecutor(CommandContext<CommandSourceStack> rawCtx, @Nullable HashedSubNodeWrapper wrapper) {
        SubCommandContext ctx = new SubCommandContext(rawCtx, new SubCommandBuilder(), this, 0);

        // Send command header
        ctx.reply(ElvenideCore.lang.common.COMMAND_HEADER);

        // Send all subcommand usages if no subnode is specified
        if (wrapper == null) {
            ctx.reply(description);
            ctx.reply(ElvenideCore.lang.common.COMMAND_USAGE_PREFIX + getUsage(null));
        }

        // Otherwise, send only the specified subnode's usage
        else {
            if (wrapper.isSubCommand()) {
                wrapper.asSubCommand().setup(ctx.subCommandData);
                ctx.reply(ctx.subCommandData.description);
            }
            ctx.reply(ElvenideCore.lang.common.COMMAND_USAGE_PREFIX + getUsage(wrapper));
        }

        return Command.SINGLE_SUCCESS;
    }

    /* </editor-fold> */

    /* <editor-fold defaultstate="collapsed" desc="Command Usage"> */

    @NotNull String getUsage(@Nullable HashedSubNodeWrapper wrapper) {
        if (wrapper == null)
            return subNodeUsages.entrySet()
                    .stream()
                    .filter(e -> HashedSubNodeWrapper.isSubCommandId(e.getKey()))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining("<br>"));
        return subNodeUsages.getOrDefault(wrapper.id(), "");
    }

    private List<String> parseUsageGroup(SubGroup group, String prefix) {
        ArrayList<String> usages = new ArrayList<>();
        prefix += group.label() + " ";

        // Parse all children's usage and concatenate into one string
        for (HashedSubNodeWrapper child : group.hashedSubNodes()) {
            usages.addAll(parseUsageTree(child, prefix));
        }

        return usages;
    }

    private String parseUsageCommand(SubCommand command, String prefix) {
        StringBuilder argUsages = new StringBuilder();
        prefix += command.label() + " ";

        // Add all arguments
        SubCommandBuilder argBuilder = new SubCommandBuilder();
        command.setup(argBuilder);
        for (SubArgumentBuilder subArg : argBuilder.subArgs) {
            String openBracket = subArg.required ? "<" : "[";
            String closeBracket = subArg.required ? ">" : "]";
            argUsages.append(openBracket)
                    .append(subArg.label)
                    .append(": ")
                    .append(subArg.getTypeName())
                    .append(closeBracket)
                    .append(" ");
        }

        prefix += argUsages.toString().strip();
        return prefix;
    }

    private List<String> parseUsageTree(HashedSubNodeWrapper currentWrapper, String prefix) {
        List<String> result;

        if (currentWrapper.isSubGroup())
            result = parseUsageGroup(currentWrapper.asSubGroup(), prefix);

        else if (currentWrapper.isSubCommand())
            result = List.of(parseUsageCommand(currentWrapper.asSubCommand(), prefix));

        else
            result = List.of();

        subNodeUsages.put(currentWrapper.id(), String.join("<br>", result));
        return result;
    }

    /* </editor-fold> */

    /* <editor-fold defaultstate="collapsed" desc="Command Tree Generation"> */

    private RequiredArgumentBuilder<CommandSourceStack, ?> getArgumentTree(LinkedList<SubArgumentBuilder> remainingArgs, HashedSubNodeWrapper wrapper, SubCommandBuilder argBuilder, int specifiedArgs) {
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
                                ElvenideCore.text.deserialize("<aqua>\\" + openBracket + arg.label + closeBracket + "</aqua>")
                        )));
                return builder.buildFuture();
            });

        // If there are more arguments to add, add them recursively
        if (!remainingArgs.isEmpty())
            argNode.then(getArgumentTree(remainingArgs, wrapper, argBuilder, specifiedArgs + 1));

        return argNode;
    }

    private LiteralArgumentBuilder<CommandSourceStack> getNodeTree(HashedSubNodeWrapper wrapper) {
        // Create subcommand literal
        LiteralArgumentBuilder<CommandSourceStack> current = Commands.literal(wrapper.asSubNode().label());

        // If it is a subgroup, add its child nodes to current node
        if (wrapper.isSubGroup()) {
            for (HashedSubNodeWrapper subWrapper : wrapper.asSubGroup().hashedSubNodes()) {
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

                // Add help executor to current subcommand node
                current.executes(context -> helpExecutor(context, wrapper));
            }

            // Otherwise, add executor on current leaf node
            else
                current.executes(context -> validatedExecutor(wrapper, new SubCommandContext(context, argBuilder, this, 0)));
        }

        return current;
    }

    /* </editor-fold> */

}
