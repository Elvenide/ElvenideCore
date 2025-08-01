package com.elvenide.core.providers.menu;

import com.elvenide.core.api.PublicAPI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a slot in a CoreMenu, with useful helper utilities.
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public class MenuSlot {

    protected final CoreMenu coreMenu;
    protected final int slot;
    protected final SlotManager slotManager;

    int rangeStart = 0;
    int rangeEnd = 0;
    boolean isRange = false;
    int populatedAreaLeft = 0;
    int populatedAreaRight = 0;
    int populatedAreaTop = 0;
    int populatedAreaBottom = 0;
    boolean isPopulatedArea = false;
    int maxPage = Integer.MAX_VALUE;

    @ApiStatus.Internal
    MenuSlot(CoreMenu coreMenu, int slot, SlotManager slotManager) {
        this.coreMenu = coreMenu;
        this.slot = slot;
        this.slotManager = slotManager;
    }

    /**
     * Returns whether the current slot is the specified slot.
     * @param slot Slot, zero-indexed
     * @return Whether the slot is the specified slot
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean is(int slot) {
        return this.slot == slot;
    }

    /**
     * Returns whether the current slot is within the specified range of slots.
     * @param startSlot First slot, inclusive and zero-indexed
     * @param endSlot Last slot, inclusive and zero-indexed
     * @return Whether the slot is within the range
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isWithin(int startSlot, int endSlot) {
        return slot >= startSlot && slot <= endSlot;
    }

    /**
     * Returns whether the current slot is within the specified range of horizontal rows.
     * @param startRow First row, inclusive and zero-indexed
     * @param endRow Last row, inclusive and zero-indexed
     * @return Whether the slot is within the range
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isWithinRow(int startRow, int endRow) {
        return isWithin(startRow * 9, (endRow + 1) * 9 - 1);
    }

    /**
     * Returns whether the current slot is within the specified range of vertical columns.
     * @param startCol First column, inclusive and zero-indexed
     * @param endCol Last column, inclusive and zero-indexed
     * @return Whether the slot is within the range
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isWithinCol(int startCol, int endCol) {
        return slot % 9 >= startCol && slot % 9 <= endCol;
    }

    /**
     * Returns whether the current slot is within the specified rectangular area.
     * @param x1 First column, inclusive and zero-indexed
     * @param y1 First row, inclusive and zero-indexed
     * @param x2 Last column, inclusive and zero-indexed
     * @param y2 Last row, inclusive and zero-indexed
     * @return Whether the slot is within the area
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isWithin(int x1, int y1, int x2, int y2) {
        return isWithinRow(y1, y2) && isWithinCol(x1, x2);
    }

    /**
     * Returns the current slot.
     * @return The current slot
     */
    @PublicAPI
    @Contract(pure = true)
    public int value() {
        return slot;
    }

    /**
     * Returns the absolute index of the current slot, based on the current page.
     * @return The absolute index
     */
    @PublicAPI
    @Contract(pure = true)
    public int index() {
        int page = Math.min(maxPage, slotManager.getPage());

        // If this slot is in a populated area, then adjust the slot index
        if (isPopulatedArea) {
            int populatedIndex = slot - populatedAreaTop * 9;
            int row = populatedIndex / 9;
            populatedIndex -= (8 - populatedAreaRight) * row + populatedAreaLeft * (row + 1);

            // Add dimensional area of populated region multiplied by page index to get paginated index
            int area = (populatedAreaRight - populatedAreaLeft + 1) * (populatedAreaBottom - populatedAreaTop + 1);
            return populatedIndex + (page-1) * area;
        }

        // If the slot is in a range, then adjust the slot index
        if (isRange) {
            int rangeIndex = slot - rangeStart;
            int length = rangeEnd - rangeStart + 1;
            return rangeIndex + (page-1) * length;
        }

        // Otherwise, return the usual slot index
        return slot + (page-1) * 9 * coreMenu.getRows();
    }

    /**
     * Returns the icon of the current slot.
     * @return The icon
     */
    @PublicAPI
    @Contract(pure = true)
    public ItemStack icon() {
        return slotManager.getInv().getItem(slot);
    }

    /**
     * Sets the icon of the current slot.
     * @param icon The icon
     */
    @PublicAPI
    public void setIcon(@Nullable ItemStack icon) {
        slotManager.getInv().setItem(slot, icon);
    }

    /**
     * Sets the icon of the current slot and adds a click handler.
     * @param icon The icon
     * @param clickHandler The click handler
     */
    @PublicAPI
    public void setIcon(@NotNull ItemStack icon, @NotNull Consumer<ClickedMenuSlot> clickHandler) {
        setIcon(icon);
        slotManager.addClickHandler(slot, clickHandler);
    }

    /**
     * Sets the current slot as a button that goes to the next page.
     * Automatically prevents the player from going beyond the last page.
     * @param icon The icon
     * @param maxPaginatedIconsPerPage Function that returns the maximum number of icons in any paginated area per page
     * @param iconListSizes Function that returns the sizes of each icon list being used to populate this inventory
     */
    @PublicAPI
    public void setNextPageButton(@NotNull ItemStack icon, Supplier<Integer> maxPaginatedIconsPerPage, Supplier<List<Integer>> iconListSizes) {
        setIcon(icon, clickedSlot -> {
            int maxIconListSize = iconListSizes.get().stream().mapToInt(i -> i).max().orElse(0);
            int maxIconsPerPage = maxPaginatedIconsPerPage.get();

            // If max icons per page is 0 or less, ignore
            if (maxIconsPerPage <= 0)
                return;

            // Calculate max page
            int maxPage = (maxIconListSize + maxIconsPerPage - 1) / maxIconsPerPage;

            // Prevent player from going beyond last page
            if (slotManager.getPage() >= maxPage)
                return;
            slotManager.toNextPage();
        });
    }

    /**
     * Sets the current slot as a button that goes to the previous page.
     * Automatically prevents the player from going beyond the first page.
     * @param icon The icon
     */
    @PublicAPI
    public void setPrevPageButton(@NotNull ItemStack icon) {
        setIcon(icon, clickedSlot -> {
            // Prevent player from going beyond first page
            if (slotManager.getPage() <= 1)
                return;
            slotManager.toPrevPage();
        });
    }

}
