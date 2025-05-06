package com.elvenide.core.providers;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class PermissionProvider extends Provider {
    @ApiStatus.Internal
    public PermissionProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Checks if a sender has a permission.
     * Supports negated permissions that start with a "-", which will check if the sender doesn't have the permission.
     * Unlike <code>has()</code>, this method will not always return true for opped/console senders -- it requires the permission
     * to be explicitly set on the sender.
     * @param sender Sender (player or console)
     * @param permission Permission, optionally negated
     * @return Boolean
     */
    public boolean hasExplicitly(CommandSender sender, String permission) {
        if (permission.startsWith("-")) {
            Permission permNode = new Permission(permission.substring(1), PermissionDefault.FALSE);
            return !sender.hasPermission(permNode);
        }

        Permission permNode = new Permission(permission, PermissionDefault.FALSE);
        return sender.hasPermission(permNode);
    }

    /**
     * Checks if a sender has a permission.
     * Supports negated permissions that start with a "-", which will check if the sender doesn't have the permission.
     * @param sender Sender (player or console)
     * @param permission Permission, optionally negated
     * @return Boolean
     */
    public boolean has(CommandSender sender, String permission) {
        if (permission.startsWith("-"))
            return !sender.hasPermission(permission.substring(1));

        return sender.hasPermission(permission);
    }
}
