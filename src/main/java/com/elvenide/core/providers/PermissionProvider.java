package com.elvenide.core.providers;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class PermissionProvider extends Provider {
    @ApiStatus.Internal
    public PermissionProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Checks if a sender explicitly has a permission.
     * <p>
     * <i>This method is deprecated due to its previous support for negated permissions.
     * There is no such thing as "explicitly not having" a permission; that is simply the same as "not having" it.</i>h
     * @param sender Sender (player or console)
     * @param permission Permission, optionally negated
     * @return Boolean
     * @deprecated Use {@link #has(Permissible, String)} with the explicit-prefix ("#") at the start of the permission
     */
    @Deprecated(since = "0.0.15", forRemoval = true)
    @Contract(pure = true)
    public boolean hasExplicitly(CommandSender sender, String permission) {
        if (permission.startsWith("-")) {
            Permission permNode = new Permission(permission.substring(1), PermissionDefault.FALSE);
            return !sender.hasPermission(permNode);
        }

        Permission permNode = new Permission(permission, PermissionDefault.FALSE);
        return sender.hasPermission(permNode);
    }

    @Contract(pure = true)
    private boolean hasExplicit(Permissible sender, String permission) {
        permission = permission.replace("\\", ""); // Remove escape characters
        Permission permNode = new Permission(permission, PermissionDefault.FALSE);
        return sender.hasPermission(permNode);
    }

    @Contract(pure = true)
    private boolean hasNegated(Permissible sender, String permission) {
        permission = permission.replace("\\", ""); // Remove escape characters
        return !sender.hasPermission(permission);
    }

    /**
     * Checks if a sender has a permission.
     * <p>
     * Permissions can start with the following prefixes for special behavior:
     * <ul>
     * <li>"-" checks if the user doesn't have the permission</li>
     * <li>"#" checks if the user explicitly has the permission set (ignores op)</li>
     * </ul>
     * <p>
     * You can use backslashes ("\") as escape characters to prevent the prefix special behavior.
     * @param user User (player or console)
     * @param permission Permission, with optional negated (-) and/or explicit (#) prefix
     * @return Boolean
     */
    @Contract(pure = true)
    public boolean has(Permissible user, String permission) {
        // Negated
        if (permission.startsWith("-"))
            return hasNegated(user, permission.substring(1));

        // Negated-Explicit (just an alias for Negated)
        else if (permission.startsWith("#-") || permission.startsWith("-#"))
            return hasNegated(user, permission.substring(2));

        // Explicit
        else if (permission.startsWith("#"))
            return hasExplicit(user, permission.substring(1));

        permission = permission.replace("\\", ""); // Remove escape characters
        return user.hasPermission(permission);
    }
}
