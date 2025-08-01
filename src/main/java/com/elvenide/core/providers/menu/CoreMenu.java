package com.elvenide.core.providers.menu;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A powerful yet simple 2-inventory GUI menu system.
 * <p></p>
 * Create a subclass of {@link CoreMenu} and implement {@link #onDisplay()}.
 * Use the {@link #top} and {@link #bottom} fields to access the respective inventories of the menu.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public abstract class CoreMenu implements InventoryHolder {

    Inventory inventory = null;
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
     * Creates a new ElvenideCore GUI menu.
     */
    @PublicAPI
    public CoreMenu() {}

    /**
     * Returns the title of the menu.
     * <p></p>
     * Supports a single <code>{}</code> placeholder, which will be replaced with the viewing player's name.
     * @return String title, with ElvenideCore MiniMessage tag support
     */
    @Contract(pure = true)
    protected abstract String getTitle();

    /**
     * Returns the number of rows in the menu.
     * @return Number of rows (between 1 and 6)
     */
    @Contract(pure = true)
    protected abstract int getRows();

    /**
     * Called when the menu is opened or refreshed.
     */
    @ApiStatus.OverrideOnly
    protected abstract void onDisplay();

    /**
     * Called when the menu is closed.
     */
    @SuppressWarnings("EmptyMethod")
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
        // Manually close any existing CoreMenu the player is currently viewing
        if (player.getOpenInventory().getTopInventory().getHolder() instanceof CoreMenu)
            player.closeInventory();

        // Save the viewer, cache the bottom inventory, create the top inventory if needed, and open the menu
        this.viewer = player;
        if (bottomCache == null)
            bottomCache = player.getInventory().getStorageContents();
        if (inventory == null)
            this.inventory = Bukkit.createInventory(this, 9 * getRows(), Core.text.deserialize(getTitle(), viewer.getName()));
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
        top.populatedItems.clear();
        bottom.populatedItems.clear();
        top.maxPopulatedIconsPerPage.set(0);
        bottom.maxPopulatedIconsPerPage.set(0);
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
     * Returns the player viewing the menu.
     * @return Player
     */
    @PublicAPI
    @Contract(pure = true)
    public Player getViewer() {
        return viewer;
    }

    /**
     * Returns the top Bukkit Inventory of the menu.
     * <p></p>
     * This method is used internally to implement the InventoryHolder interface.
     * For use in your plugin, use {@link SlotManager#getInv() #top.getInv()} instead.
     * @return Inventory (top inventory)
     */
    @ApiStatus.Internal
    @Override
    public final @NotNull Inventory getInventory() {
        return inventory;
    }
}
