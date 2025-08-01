package com.elvenide.core.providers.menu;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * A powerful yet simple 2-inventory GUI menu system.
 * <p>
 * Create a subclass of {@link CoreMenu} and implement {@link #onDisplay()}.
 * Use the {@link #top} and {@link #bottom} fields to access the respective inventories of the menu.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public abstract class CoreMenu {

    private final int rows;
    private final Inventory inventory;
    private Player viewer;
    private ItemStack[] bottomCache = null;

    /**
     * Represents the top inventory (chest inventory) of the menu.
     */
    @PublicAPI
    public final SlotManager top = new SlotManager(this, true);

    /**
     * Represents the bottom inventory (player inventory) of the menu.
     */
    @PublicAPI
    public final SlotManager bottom = new SlotManager(this, false);

    /**
     * Creates a new menu.
     * <p>
     * This constructor is not meant to be called directly.
     * Instead, create a subclass extending {@link CoreMenu} and override {@link #onDisplay()} and/or {@link #onClose()}.
     * @param title The title of the menu, with full ElvenideCore MiniMessage support
     * @param rows The number of rows in the menu
     * @param titlePlaceholders Optional placeholder values for the title
     */
    @PublicAPI
    public CoreMenu(String title, int rows, Object... titlePlaceholders) {
        this.rows = rows;
        this.inventory = Bukkit.createInventory(null, 9 * rows, Core.text.deserialize(title, titlePlaceholders));
    }

    /**
     * Called when the menu is opened or refreshed.
     */
    @ApiStatus.OverrideOnly
    protected abstract void onDisplay();

    /**
     * Called when the menu is closed.
     */
    @ApiStatus.OverrideOnly
    protected void onClose() {}

    @ApiStatus.Internal
    void restoreBottomInv() {
        // Restore the bottom inventory cached contents
        viewer.getInventory().clear();
        if (bottomCache == null) return;
        viewer.getInventory().setStorageContents(bottomCache);
    }

    /**
     * Opens the menu.
     * @param player Player to show the menu to
     */
    @PublicAPI
    public void open(Player player) {
        this.viewer = player;
        if (bottomCache == null)
            bottomCache = player.getInventory().getStorageContents();
        player.openInventory(inventory);
        refresh();
        new CoreMenuListener(this);
    }

    /**
     * Calls the {@link #onDisplay()} method again, updating the contents of the menu.
     */
    @PublicAPI
    public void refresh() {
        // Clear the inventories
        inventory.clear();
        viewer.getInventory().setStorageContents(new ItemStack[0]);
        onDisplay();
    }

    /**
     * Closes the menu.
     */
    @PublicAPI
    public void close() {
        inventory.close();
    }

    /**
     * Returns the menu's underlying Bukkit Inventory.
     * @return Inventory
     */
    @PublicAPI
    @Contract(pure = true)
    public Inventory getInv() {
        return inventory;
    }

    /**
     * Returns the player viewing the menu.
     * @return Player
     */
    @PublicAPI
    @Contract(pure = true)
    public Player getViewer() {
        return viewer;
    }

    /**
     * Returns the number of rows in the menu.
     * @return Rows
     */
    @PublicAPI
    @Contract(pure = true)
    public int getRows() {
        return rows;
    }

}
