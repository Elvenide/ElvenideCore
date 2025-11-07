package com.elvenide.core.providers.menu;

import com.elvenide.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

class CoreMenuListener implements Listener {

    private final CoreMenu coreMenu;

    CoreMenuListener(CoreMenu coreMenu) {
        this.coreMenu = coreMenu;
        Core.plugin.registerListeners(this);
    }

    @EventHandler
    public void onTopAction(InventoryClickEvent event) {
        if (event.getClickedInventory() != coreMenu.top.getInv()) return;

        // Cancel all actions
        event.setCancelled(true);

        // Get the clicked slot
        ClickType click = event.getClick();
        int slot = event.getSlot();

        // Ignore non-clicks
        if (!click.isLeftClick() && !click.isRightClick())
            return;

        // Run actions
        coreMenu.top.click(slot, click);
    }

    @EventHandler
    public void onBottomAction(InventoryClickEvent event) {
        if (event.getClickedInventory() != coreMenu.bottom.getInv()) return;

        // Cancel all actions
        event.setCancelled(true);

        // Get the clicked slot
        ClickType click = event.getClick();
        int slot = event.getSlot();

        // Ignore non-clicks
        if (!click.isLeftClick() && !click.isRightClick())
            return;

        // Run actions
        coreMenu.bottom.click(slot, click);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != coreMenu.top.getInv()) return;
        Core.plugin.unregisterListeners(this);
        coreMenu.restoreBottomInv();
        coreMenu.onClose();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Core.plugin.unregisterListeners(this);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (Core.plugin.get() != event.getPlugin())
            return;
        coreMenu.restoreBottomInv();
        coreMenu.onClose();
    }

}
