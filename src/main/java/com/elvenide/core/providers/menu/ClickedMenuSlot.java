package com.elvenide.core.providers.menu;

import com.elvenide.core.api.PublicAPI;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * Represents a slot in a CoreMenu that has been clicked.
 * @author <a href="https://github.com/Elvenide">Elvenide</a>
 * @since 0.0.15
 */
public class ClickedMenuSlot extends MenuSlot {

    protected final ClickType clickType;

    @ApiStatus.Internal
    ClickedMenuSlot(CoreMenu coreMenu, int slot, SlotManager slotManager, ClickType clickType) {
        super(coreMenu, slot, slotManager);
        this.clickType = clickType;
    }

    /**
     * Checks if the click was a left click or shift-left click
     * @return Boolean
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isLeftClick() {
        return clickType.isLeftClick();
    }

    /**
     * Checks if the click was a right click or shift-right click
     * @return Boolean
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isRightClick() {
        return clickType.isRightClick();
    }

    /**
     * Checks if the click was a shift click
     * @return Boolean
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public boolean isShiftClick() {
        return clickType.isShiftClick();
    }
}
