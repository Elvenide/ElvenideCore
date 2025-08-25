package com.elvenide.core.providers.menu;

import com.elvenide.core.api.PublicAPI;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Represents one of two inventories in a CoreMenu.
 * <p>
 * Use {@link #slot(int)} to access a specific slot and, for example, add an item at that slot.
 * Or use the various <code>populate</code> methods to add groups of items to the menu (with pagination support).
 * Or use the various <code>assign</code> methods to add a single item to several slots (without pagination).
 * @author <a href="https://elvenide.com">Elvenide</a>
 * @since 0.0.15
 */
public class SlotManager {
    final boolean isTop;
    private final CoreMenu coreMenu;
    private int page = 1;
    private final HashMap<Integer, Consumer<ClickedMenuSlot>> clickHandlers = new HashMap<>();
    final ArrayList<Integer> populatedItems = new ArrayList<>();
    final AtomicInteger maxPopulatedIconsPerPage = new AtomicInteger(0);

    @ApiStatus.Internal
    SlotManager(CoreMenu coreMenu, boolean isTop) {
        this.isTop = isTop;
        this.coreMenu = coreMenu;
    }

    /**
     * Returns the current page.
     * @return Page, starting at a minimum of 1
     */
    @PublicAPI
    @Contract(pure = true)
    public int getPage() {
        return page;
    }

    /**
     * Navigates to the specified page.
     * Automatically refreshes the menu to display the new page contents.
     * @param page Page, starting at a minimum of 1
     */
    @PublicAPI
    public void setPage(int page) {
        this.page = Math.max(1, page); // Ensure minimum page
        coreMenu.refresh();
    }

    /**
     * Navigates to the next page.
     * Automatically refreshes the menu to display the new page contents.
     */
    @PublicAPI
    public void toNextPage() {
        setPage(page + 1);
    }

    /**
     * Navigates to the previous page.
     * Automatically refreshes the menu to display the new page contents.
     */
    @PublicAPI
    public void toPrevPage() {
        if (page == 1) return;
        setPage(page - 1);
    }

    /**
     * Returns the underlying Bukkit Inventory.
     * @return Inventory
     */
    @PublicAPI
    @Contract(pure = true)
    public Inventory getInv() {
        if (isTop)
            return coreMenu.inventory;
        return coreMenu.getViewer().getInventory();
    }

    /**
     * Returns a {@link MenuSlot} with helpful utilities at the specified slot number.
     * @param slot Slot, zero-indexed
     * @return MenuSlot
     */
    @PublicAPI
    @Contract(pure = true)
    public MenuSlot slot(int slot) {
        return new MenuSlot(coreMenu, slot, this);
    }

    private void populateRangeInternal(int startSlot, int endSlot, List<@Nullable ItemStack> icons, @Nullable ItemStack fallbackIcon, @Nullable Consumer<ClickedMenuSlot> clickHandler) {
        int length = endSlot - startSlot + 1;
        maxPopulatedIconsPerPage.set(Math.max(maxPopulatedIconsPerPage.get(), length));
        populatedItems.add(icons.size());

        for (int i = startSlot; i <= endSlot; i++) {
            MenuSlot slot = slot(i);
            slot.rangeStart = startSlot;
            slot.rangeEnd = endSlot;
            slot.isRange = true;
            slot.maxPage = (icons.size() - 1) / length + 1;
            int index = slot.index();
            if (index < icons.size()) {
                @Nullable ItemStack icon = icons.get(index);
                if (clickHandler != null && icon != null)
                    slot.setIcon(icon, clickedSlot -> {
                        clickedSlot.rangeStart = startSlot;
                        clickedSlot.rangeEnd = endSlot;
                        clickedSlot.isRange = true;
                        clickedSlot.maxPage = (icons.size() - 1) / length + 1;
                        clickHandler.accept(clickedSlot);
                    });
                else if (icon != null)
                    slot.setIcon(icon);
                else if (fallbackIcon != null)
                    slot.setIcon(fallbackIcon);
            }
            else if (fallbackIcon != null)
                slot.setIcon(fallbackIcon);
        }
    }

    /**
     * Populates the specified range of slots with the provided icons.
     * @param startSlot First slot in the range, zero-indexed (inclusive)
     * @param endSlot Last slot in the range, zero-indexed (inclusive)
     * @param icons Icons to populate the range with, in order
     * @since 0.0.15
     */
    @PublicAPI
    public void populateRange(int startSlot, int endSlot, @NotNull List<@Nullable ItemStack> icons) {
        populateRangeInternal(startSlot, endSlot, icons, null, null);
    }

    /**
     * Populates the specified range of slots with the provided icons.
     * @param startSlot First slot in the range, zero-indexed (inclusive)
     * @param endSlot Last slot in the range, zero-indexed (inclusive)
     * @param icons Icons to populate the range with, in order
     * @param clickHandler Click handler for every non-null/non-fallback icon in the range
     * @since 0.0.15
     */
    @PublicAPI
    public void populateRange(int startSlot, int endSlot, @NotNull List<@Nullable ItemStack> icons, @NotNull Consumer<ClickedMenuSlot> clickHandler) {
        populateRangeInternal(startSlot, endSlot, icons, null, clickHandler);
    }

    /**
     * Populates the specified range of slots with the provided icons.
     * @param startSlot First slot in the range, zero-indexed (inclusive)
     * @param endSlot Last slot in the range, zero-indexed (inclusive)
     * @param icons Icons to populate the range with, in order
     * @param fallbackIcon Fallback/background icon used in parts of the range that are not populated by the icons list
     * @since 0.0.15
     */
    @PublicAPI
    public void populateRange(int startSlot, int endSlot, @NotNull List<@Nullable ItemStack> icons, @Nullable ItemStack fallbackIcon) {
        populateRangeInternal(startSlot, endSlot, icons, fallbackIcon, null);
    }

    /**
     * Populates the specified range of slots with the provided icons.
     * @param startSlot First slot in the range, zero-indexed (inclusive)
     * @param endSlot Last slot in the range, zero-indexed (inclusive)
     * @param icons Icons to populate the range with, in order
     * @param fallbackIcon Fallback/background icon used in parts of the range that are not populated by the icons list
     * @param clickHandler Click handler for every non-null/non-fallback icon in the range
     * @since 0.0.15
     */
    @PublicAPI
    public void populateRange(int startSlot, int endSlot, @NotNull List<@Nullable ItemStack> icons, @Nullable ItemStack fallbackIcon, @NotNull Consumer<ClickedMenuSlot> clickHandler) {
        populateRangeInternal(startSlot, endSlot, icons, fallbackIcon, clickHandler);
    }

    private void populateAreaInternal(int col1, int row1, int col2, int row2, List<@Nullable ItemStack> icons, @Nullable ItemStack fallbackIcon, @Nullable Consumer<ClickedMenuSlot> clickHandler) {
        int area = (col2 - col1 + 1) * (row2 - row1 + 1);
        maxPopulatedIconsPerPage.set(Math.max(maxPopulatedIconsPerPage.get(), area));
        populatedItems.add(icons.size());

        for (int y = row1; y <= row2; y++) {
            for (int x = col1; x <= col2; x++) {
                MenuSlot slot = slot(x + y * 9);
                slot.populatedAreaLeft = col1;
                slot.populatedAreaRight = col2;
                slot.populatedAreaTop = row1;
                slot.populatedAreaBottom = row2;
                slot.isPopulatedArea = true;
                slot.maxPage = (icons.size() - 1) / area + 1;
                int index = slot.index();
                if (index < icons.size()) {
                    @Nullable ItemStack icon = icons.get(index);
                    if (clickHandler != null && icon != null)
                        slot.setIcon(icon, clickedSlot -> {
                            clickedSlot.populatedAreaLeft = col1;
                            clickedSlot.populatedAreaRight = col2;
                            clickedSlot.populatedAreaTop = row1;
                            clickedSlot.populatedAreaBottom = row2;
                            clickedSlot.isPopulatedArea = true;
                            clickedSlot.maxPage = (icons.size() - 1) / area + 1;
                            clickHandler.accept(clickedSlot);
                        });
                    else if (icon != null)
                        slot.setIcon(icon);
                    else if (fallbackIcon != null)
                        slot.setIcon(fallbackIcon);
                }
                else if (fallbackIcon != null)
                    slot.setIcon(fallbackIcon);
            }
        }
    }

    /**
     * Populates the specified rectangular region of slots with the provided icons.
     * @param col1 First column, inclusive and zero-indexed (i.e. smallest x coordinate, where leftmost x is 0)
     * @param row1 First row, inclusive and zero-indexed (i.e. smallest y coordinate, where topmost y is 0)
     * @param col2 Last column, inclusive and zero-indexed (i.e. biggest x coordinate, where leftmost x is 0)
     * @param row2 Last row, inclusive and zero-indexed (i.e. biggest y coordinate, where topmost y is 0)
     * @param icons Icons to populate the area with, in order from top-left to bottom-right
     * @since 0.0.15
     */
    @PublicAPI
    public void populateArea(int col1, int row1, int col2, int row2, @NotNull List<@Nullable ItemStack> icons) {
        populateAreaInternal(col1, row1, col2, row2, icons, null, null);
    }

    /**
     * Populates the specified rectangular region of slots with the provided icons.
     * @param col1 First column, inclusive and zero-indexed (i.e. smallest x coordinate, where leftmost x is 0)
     * @param row1 First row, inclusive and zero-indexed (i.e. smallest y coordinate, where topmost y is 0)
     * @param col2 Last column, inclusive and zero-indexed (i.e. biggest x coordinate, where leftmost x is 0)
     * @param row2 Last row, inclusive and zero-indexed (i.e. biggest y coordinate, where topmost y is 0)
     * @param icons Icons to populate the area with, in order from top-left to bottom-right
     * @param clickHandler Click handler for every non-null/non-fallback icon in the area
     * @since 0.0.15
     */
    @PublicAPI
    public void populateArea(int col1, int row1, int col2, int row2, @NotNull List<@Nullable ItemStack> icons, @NotNull Consumer<ClickedMenuSlot> clickHandler) {
        populateAreaInternal(col1, row1, col2, row2, icons, null, clickHandler);
    }

    /**
     * Populates the specified rectangular region of slots with the provided icons.
     * @param col1 First column, inclusive and zero-indexed (i.e. smallest x coordinate, where leftmost x is 0)
     * @param row1 First row, inclusive and zero-indexed (i.e. smallest y coordinate, where topmost y is 0)
     * @param col2 Last column, inclusive and zero-indexed (i.e. biggest x coordinate, where leftmost x is 0)
     * @param row2 Last row, inclusive and zero-indexed (i.e. biggest y coordinate, where topmost y is 0)
     * @param icons Icons to populate the area with, in order from top-left to bottom-right
     * @param fallbackIcon Fallback/background icon used in parts of the area that are not populated by the icons list
     * @since 0.0.15
     */
    @PublicAPI
    public void populateArea(int col1, int row1, int col2, int row2, @NotNull List<@Nullable ItemStack> icons, @Nullable ItemStack fallbackIcon) {
        populateAreaInternal(col1, row1, col2, row2, icons, fallbackIcon, null);
    }

    /**
     * Populates the specified rectangular region of slots with the provided icons.
     * @param col1 First column, inclusive and zero-indexed (i.e. smallest x coordinate, where leftmost x is 0)
     * @param row1 First row, inclusive and zero-indexed (i.e. smallest y coordinate, where topmost y is 0)
     * @param col2 Last column, inclusive and zero-indexed (i.e. biggest x coordinate, where leftmost x is 0)
     * @param row2 Last row, inclusive and zero-indexed (i.e. biggest y coordinate, where topmost y is 0)
     * @param icons Icons to populate the area with, in order from top-left to bottom-right
     * @param fallbackIcon Fallback/background icon used in parts of the area that are not populated by the icons list
     * @param clickHandler Click handler for every non-null/non-fallback icon in the area
     * @since 0.0.15
     */
    @PublicAPI
    public void populateArea(int col1, int row1, int col2, int row2, @NotNull List<@Nullable ItemStack> icons, @Nullable ItemStack fallbackIcon, @NotNull Consumer<ClickedMenuSlot> clickHandler) {
        populateAreaInternal(col1, row1, col2, row2, icons, fallbackIcon, clickHandler);
    }

    /**
     * Deprecated to separate naming of pagination-supported methods (populate methods) from single-item methods (assign methods).
     * @deprecated Use {@link #assignBorder(int, int, int, int, ItemStack)} instead.
     */
    @Deprecated(since = "0.0.17", forRemoval = true)
    public void populateBorder(int col1, int row1, int col2, int row2, @Nullable ItemStack icon) {
        assignBorder(col1, row1, col2, row2, icon);
    }

    /**
     * Assigns the provided icon to the specified rectangular region's outer border slots.
     * @param col1 First column, inclusive and zero-indexed (i.e. smallest x coordinate, where leftmost x is 0)
     * @param row1 First row, inclusive and zero-indexed (i.e. smallest y coordinate, where topmost y is 0)
     * @param col2 Last column, inclusive and zero-indexed (i.e. biggest x coordinate, where leftmost x is 0)
     * @param row2 Last row, inclusive and zero-indexed (i.e. biggest y coordinate, where topmost y is 0)
     * @param icon Icon to assign to the border
     * @since 0.0.17
     */
    @PublicAPI
    public void assignBorder(int col1, int row1, int col2, int row2, @Nullable ItemStack icon) {
        for (int x = col1; x <= col2; x++) {
            slot(x + row1 * 9).setIcon(icon);
            slot(x + row2 * 9).setIcon(icon);
        }
        for (int y = row1; y <= row2; y++) {
            slot(col1 + y * 9).setIcon(icon);
            slot(col2 + y * 9).setIcon(icon);
        }
    }

    /**
     * Assigns the provided icon to the specified range of slots.
     * @param startSlot First slot in the range, zero-indexed (inclusive)
     * @param endSlot Last slot in the range, zero-indexed (inclusive)
     * @param icon Icon to assign to the range
     * @since 0.0.17
     */
    @PublicAPI
    public void assignRange(int startSlot, int endSlot, @Nullable ItemStack icon) {
        for (int i = startSlot; i <= endSlot; i++) {
            slot(i).setIcon(icon);
        }
    }

    /**
     * Assigns the provided icon to the specified rectangular region's inner area slots.
     * @param col1 First column, inclusive and zero-indexed (i.e. smallest x coordinate, where leftmost x is 0)
     * @param row1 First row, inclusive and zero-indexed (i.e. smallest y coordinate, where topmost y is 0)
     * @param col2 Last column, inclusive and zero-indexed (i.e. biggest x coordinate, where leftmost x is 0)
     * @param row2 Last row, inclusive and zero-indexed (i.e. biggest y coordinate, where topmost y is 0)
     * @param icon Icon to assign to the area
     * @since 0.0.17
     */
    @PublicAPI
    public void assignArea(int col1, int row1, int col2, int row2, @Nullable ItemStack icon) {
        for (int y = row1; y <= row2; y++) {
            for (int x = col1; x <= col2; x++) {
                MenuSlot slot = slot(x + y * 9);
                slot.setIcon(icon);
            }
        }
    }

    protected void addClickHandler(int slot, Consumer<ClickedMenuSlot> handler) {
        clickHandlers.put(slot, handler);
    }

    protected void clearClickHandlers() {
        clickHandlers.clear();
    }

    protected void click(int slot, ClickType click) {
        if (!clickHandlers.containsKey(slot)) return;
        clickHandlers.get(slot).accept(new ClickedMenuSlot(coreMenu, slot, this, click));
    }
}
