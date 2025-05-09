package com.elvenide.core.providers.commands;

import com.elvenide.core.Core;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Generates a command help/usage message for a single subnode.
 * <p>
 * A subgroup will display a list of its children.<br/>
 * A subcommand will display its own usage, including any arguments.
 * @since 0.0.15
 */
class UsageGenerator {

    private final NodeWrapper self;
    private final String prefix;

    UsageGenerator(NodeWrapper self) {
        this.self = self;
        prefix = getStringPathToSelf();
    }

    private String getStringPathToSelf() {
        LinkedList<String> path = new LinkedList<>();

        NodeWrapper current = self;
        while (current != null) {
            if (current.isSubCommand())
                path.addFirst(displaySubCommand(current.asSubCommand()));
            else
                path.addFirst(displaySubGroup(current.asSubGroup()));

            current = current.parent();
        }

        return String.join(" ", path);
    }

    private String displaySubGroup(SubGroup group) {
        String output = Core.lang.common.SUBGROUP_HELP_FORMATTING.formatted(group.label());

        ArrayList<String> childUsages = new ArrayList<>();
        for (NodeWrapper child : group.getChildNodes())
            if (child.isSubCommand())
                childUsages.add(prefix + " " + output + " " + displaySubCommand(child.asSubCommand()));

        if (!childUsages.isEmpty()) {
            if (childUsages.size() > 8) {
                childUsages = new ArrayList<>(childUsages.subList(0, 8));
                childUsages.add("...");
            }

            output = "<hover:show_text:\"%s\">%s</hover>".formatted(String.join(
                "<br>",
                childUsages
                    .stream()
                    .map(line -> Core.lang.common.COMMAND_USAGE_PREFIX + line)
                    .toList()
            ), output);
        }

        return output;
    }

    private String displaySubCommand(SubCommand command) {
        StringBuilder argUsages = new StringBuilder();
        String output = Core.lang.common.SUBCOMMAND_HELP_FORMATTING.formatted(command.label()) + " ";

        // Add all arguments
        SubCommandBuilder argBuilder = new SubCommandBuilder();
        command.setup(argBuilder);
        for (SubArgumentBuilder subArg : argBuilder.subArgs) {
            argUsages.append(subArg.formatted())
                .append(" ");
        }

        output += argUsages.toString().strip();
        return output;
    }

    private String displaySubNode(NodeWrapper wrapper) {
        if (wrapper.isSubCommand())
            return displaySubCommand(wrapper.asSubCommand());
        return displaySubGroup(wrapper.asSubGroup());
    }

    /// Generates either child usages (of subgroup) or own usage (of subcommand); returns null if 'page' is too large
    private List<String> generateUsages(NodeWrapper current, @Nullable CommandSender executor) {
        if (current.isSubCommand())
            return List.of(prefix);

        ArrayList<String> usages = new ArrayList<>();
        int missingPerms = 0;

        for (NodeWrapper child : current.asSubGroup().getChildNodes()) {
            if (executor != null && child.isSubCommand()) {
                SubCommandBuilder subCommandBuilder = new SubCommandBuilder();
                child.asSubCommand().setup(subCommandBuilder);
                String perm = subCommandBuilder.permission;

                if (perm != null && !Core.perms.has(executor, perm)) {
                    missingPerms++;
                    continue;
                }
            }

            usages.add(prefix + " " + displaySubNode(child));
        }

        String missingPermsMessage = Core.lang.common.COMMANDS_HIDDEN_BY_PERMS.formatted(missingPerms);
        if (usages.isEmpty()) {
            if (missingPerms > 0)
                return List.of(missingPermsMessage);
            else
                return List.of(prefix);
        }

        if (missingPerms > 0)
            usages.add(missingPermsMessage);

        return usages;
    }

    /// Generates a multi-line command usage message
    public String generate(CommandSender executor) {
        return String.join(
            "<br>",
            generateUsages(self, executor)
                .stream()
                .map(line -> Core.lang.common.COMMAND_USAGE_PREFIX + line)
                .toList()
        );
    }

}
