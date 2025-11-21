package com.elvenide.core.providers.item;

import com.elvenide.core.Core;
import com.elvenide.core.api.PublicAPI;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack item;

    @ApiStatus.Internal
    ItemBuilder(Core instance, @Nullable Material material) {
        if (instance == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        if (material == null)
            throw new IllegalArgumentException("Material cannot be null");

        this.item = ItemStack.of(material, 1);
    }

    @ApiStatus.Internal
    ItemBuilder(Core instance, @NotNull ItemStack item) {
        if (instance == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        this.item = item;
    }

    /**
     * Sets the amount of the item; defaults to 1.
     * @param amount Amount
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Sets the name of the item, with optional placeholders.
     * @param name String name
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder name(@NotNull String name, Object... placeholders) {
        return meta(m -> m.customName(nonItalicWithItalicSupport(name, placeholders)));
    }

    /**
     * Sets the name of the item, with support for a third-party placeholder plugin.
     * @param name String name
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder name(@NotNull String name, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        return meta(m -> m.customName(nonItalicWithItalicSupport(name, placeholderResolver)));
    }

    /**
     * Adds a line of lore of the item, with optional placeholders.
     * @param loreLine String lore line
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder lore(@NotNull String loreLine, Object... placeholders) {
        return meta(m -> {
            List<Component> existingLore = m.lore();
            ArrayList<Component> lore = existingLore == null ? new ArrayList<>() : new ArrayList<>(existingLore);
            lore.add(nonItalicWithItalicSupport(loreLine, placeholders));
            m.lore(lore);
        });
    }

    /**
     * Adds a line of lore of the item, with support for a third-party placeholder plugin.
     * @param loreLine String lore line
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder lore(@NotNull String loreLine, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        return meta(m -> {
            List<Component> existingLore = m.lore();
            ArrayList<Component> lore = existingLore == null ? new ArrayList<>() : new ArrayList<>(existingLore);
            lore.add(nonItalicWithItalicSupport(loreLine, placeholderResolver));
            m.lore(lore);
        });
    }

    /**
     * Sets the lore of the item.
     * @param lore List of lore lines
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder lores(@NotNull List<String> lore) {
        return meta(m -> m.lore(lore.stream().map(this::nonItalicWithItalicSupport).toList()));
    }

    /**
     * Sets the lore of the item, with support for a third-party placeholder plugin.
     * @param lore List of lore lines
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder lores(@NotNull List<String> lore, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        return meta(m -> m.lore(lore.stream().map(s -> nonItalicWithItalicSupport(s, placeholderResolver)).toList()));
    }

    /**
     * Sets the max stack size of the item.
     * @param size Max stack size
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder maxStackSize(int size) {
        if (size < 1)
            throw new IllegalArgumentException("Max stack size cannot be less than 1");

        return meta(m -> m.setMaxStackSize(size));
    }

    /**
     * Sets the owner of the player head item.
     * Silently fails if the item is not a player head.
     * @param name Player name
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder headOwner(String name) {
        return meta(SkullMeta.class, m -> m.setPlayerProfile(Bukkit.createProfile(name)));
    }

    /**
     * Sets the color of the leather armor item.
     * Silently fails if the item is not leather armor.
     * @param color Color
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder color(Color color) {
        return meta(LeatherArmorMeta.class, m -> m.setColor(color));
    }

    /**
     * Adds an enchantment to the item, with vanilla enchantment checks.
     * @param enchantment Enchantment
     * @param level Enchantment level
     * @param ignoreLevelLimit Whether to ignore level limit
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder enchant(Enchantment enchantment, int level, boolean ignoreLevelLimit) {
        return meta(m -> m.addEnchant(enchantment, level, ignoreLevelLimit));
    }

    /**
     * Sets all enchantments on the item, with vanilla enchantment checks.
     * @param enchantmentLevels Map of enchantments to their levels
     * @param ignoreLevelLimit Whether to ignore level limit
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder enchants(Map<Enchantment, Integer> enchantmentLevels, boolean ignoreLevelLimit) {
        meta(ItemMeta::removeEnchantments);
        enchantmentLevels.forEach((ench, level) -> enchant(ench, level, ignoreLevelLimit));
        return this;
    }

    /**
     * Adds an enchantment to the item, with no checks for level limit, enchantment conflicts, or whether applicable to the item.
     * @param enchantment Enchantment
     * @param level Enchantment level
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder unsafeEnchant(Enchantment enchantment, int level) {
        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Sets all enchantments on the item, with no checks for level limit, enchantment conflicts, or whether applicable to the item.
     * @param enchantmentLevels Map of enchantments to their levels
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder unsafeEnchants(Map<Enchantment, Integer> enchantmentLevels) {
        meta(ItemMeta::removeEnchantments);
        enchantmentLevels.forEach(this::unsafeEnchant);
        return this;
    }

    /**
     * Sets whether the tooltip should be shown or hidden when hovering over the item in an inventory.
     * <p>
     * This method is a limited shortcut method for <code>component(DataComponentTypes.TOOLTIP_DISPLAY, ...)</code>.
     * Use the component method directly for full customization.
     * @param show Whether to show the tooltip, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder displayTooltip(@Nullable Boolean show) {
        if (show == null)
            return resetComponent(DataComponentTypes.HIDE_TOOLTIP);
        return component(DataComponentTypes.HIDE_TOOLTIP);
    }

    /**
     * Sets whether the item is unbreakable.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.UNBREAKABLE)</code>.
     * @param unbreakable Whether the item is unbreakable, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder unbreakable(@Nullable Boolean unbreakable) {
        if (unbreakable == null)
            return resetComponent(DataComponentTypes.UNBREAKABLE);
        return component(DataComponentTypes.UNBREAKABLE, Unbreakable.unbreakable());
    }

    /**
     * Sets whether the item should show an enchantment glint.
     * This value will override default behavior, allowing adding a glint to non-enchanted items
     * and removing the glint from enchanted items.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, ...)</code>.
     * @param show Whether the item should override the enchantment glint, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder overrideEnchantGlint(@Nullable Boolean show) {
        if (show == null)
            return resetComponent(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
        return component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, show);
    }

    /**
     * Makes the item equippable to the specified equipment slot.
     * <p>
     * This method is a limited shortcut method for <code>component(DataComponentTypes.EQUIPPABLE, ...)</code>.
     * Use the component method directly for full customization.
     * @param slot Slot, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder equippable(@Nullable EquipmentSlot slot) {
        if (slot == null)
            return resetComponent(DataComponentTypes.EQUIPPABLE);
        return component(DataComponentTypes.EQUIPPABLE, Equippable.equippable(slot));
    }

    /**
     * Sets the custom model data of the item.
     * Utilizes the 1.21+ String version of custom model data.
     * <p>
     * This method is a limited shortcut method for <code>component(DataComponentTypes.CUSTOM_MODEL_DATA, ...)</code>.
     * Use the component method directly for full customization.
     * @param customModelData Custom model data, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder customModelData(@Nullable String customModelData) {
        if (customModelData == null)
            return resetComponent(DataComponentTypes.CUSTOM_MODEL_DATA);
        return component(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(customModelData));
    }

    /**
     * Sets the food properties of the item.
     * If this item is not usually consumable, you should also use {@link #consumable(float, boolean, NamespacedKey)}
     * to set the item as consumable.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.FOOD, ...)</code>.
     * @param nutrition Nutrition, the amount of hunger restored on consumption
     * @param saturation Saturation, the amount of hunger prevention given
     * @param canAlwaysEat Whether the item can always be eaten
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder food(int nutrition, float saturation, boolean canAlwaysEat) {
        return component(DataComponentTypes.FOOD, FoodProperties.food()
            .nutrition(nutrition)
            .saturation(saturation)
            .canAlwaysEat(canAlwaysEat)
        );
    }

    /**
     * Sets the consumable properties of the item.
     * By default, the consumption animation is set to an eating animation.
     * <p>
     * This method is a limited shortcut method for <code>component(DataComponentTypes.CONSUMABLE, ...)</code>.
     * Use the component method directly for full customization.
     * @param consumeSeconds Consume seconds, the time it takes to consume the item
     * @param hasConsumeParticles Whether the item has particles on consumption
     * @param sound Sound, the sound played on consumption
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder consumable(float consumeSeconds, boolean hasConsumeParticles, @NotNull NamespacedKey sound) {
        return component(DataComponentTypes.CONSUMABLE, Consumable.consumable()
            .consumeSeconds(consumeSeconds)
            .hasConsumeParticles(hasConsumeParticles)
            .animation(ItemUseAnimation.EAT)
            .sound(sound)
        );
    }

    /**
     * Sets whether the item should allow players to glide like they do with an elytra.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.GLIDER, ...)</code>.
     * @param glider Whether the item should have a glider, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder glider(@Nullable Boolean glider) {
        if (glider == null)
            return resetComponent(DataComponentTypes.GLIDER);
        return component(DataComponentTypes.GLIDER);
    }

    /**
     * Sets the model of the item, using a String key with your plugin's namespace.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.ITEM_MODEL, ...)</code>.
     * @param modelKey Model key as a string
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder itemModel(@NotNull String modelKey) {
        return itemModel(Core.keys.get(modelKey));
    }

    /**
     * Sets the model of the item, using an Enum key with your plugin's namespace.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.ITEM_MODEL, ...)</code>.
     * @param modelKey Model key as an enum
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder itemModel(@NotNull Enum<?> modelKey) {
        return itemModel(Core.keys.get(modelKey));
    }

    /**
     * Sets the model of the item, using a NamespacedKey.
     * Use {@link Core#keys} to easily generate a NamespacedKey.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.ITEM_MODEL, ...)</code>.
     * @param modelKey Model key as a NamespacedKey, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder itemModel(@Nullable NamespacedKey modelKey) {
        if (modelKey == null)
            return resetComponent(DataComponentTypes.ITEM_MODEL);
        return component(DataComponentTypes.ITEM_MODEL, modelKey);
    }

    /**
     * Allows this item to play the specified song when inserted in a jukebox.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.JUKEBOX_PLAYABLE, ...)</code>.
     * @param song Song to play, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder jukeboxPlayable(@Nullable JukeboxSong song) {
        if (song == null)
            return resetComponent(DataComponentTypes.JUKEBOX_PLAYABLE);
        return component(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayable.jukeboxPlayable(song));
    }

    /**
     * Sets the tool properties of the item.
     * <p>
     * This method is a limited shortcut method for <code>component(DataComponentTypes.TOOL, ...)</code>.
     * Use the component method directly for full customization.
     * @param miningSpeed Mining speed
     * @param durabilityPerBlock Durability damage taken per block mined
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder tool(float miningSpeed, int durabilityPerBlock) {
        return component(DataComponentTypes.TOOL, Tool.tool()
            .defaultMiningSpeed(miningSpeed)
            .damagePerBlock(durabilityPerBlock)
        );
    }

    /**
     * Sets the tooltip style of the item, using a NamespacedKey.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.TOOLTIP_STYLE, ...)</code>.
     * @param key Tooltip style key as a NamespacedKey, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder tooltipStyle(@Nullable NamespacedKey key) {
        if (key == null)
            return resetComponent(DataComponentTypes.TOOLTIP_STYLE);
        return component(DataComponentTypes.TOOLTIP_STYLE, key);
    }

    /**
     * Sets the tooltip style of the item, using an Enum.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.TOOLTIP_STYLE, ...)</code>.
     * @param key Tooltip style key as an enum
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder tooltipStyle(@NotNull Enum<?> key) {
        return tooltipStyle(Core.keys.get(key));
    }

    /**
     * Sets the tooltip style of the item, using a String key.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.TOOLTIP_STYLE, ...)</code>.
     * @param key Tooltip style key as a string
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder tooltipStyle(@NotNull String key) {
        return tooltipStyle(Core.keys.get(key));
    }

    /**
     * Attaches this item to a cooldown group, identified by a NamespacedKey.
     * When any item in a cooldown group is used, the cooldown is applied to all items in the group.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.USE_COOLDOWN, ...)</code>.
     * @param cooldownGroup Cooldown group key as a NamespacedKey
     * @param secs Cooldown in seconds
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder useCooldown(@NotNull NamespacedKey cooldownGroup, float secs) {
        return component(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(secs).cooldownGroup(cooldownGroup));
    }

    /**
     * Attaches this item to a cooldown group, identified by an Enum.
     * When any item in a cooldown group is used, the cooldown is applied to all items in the group.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.USE_COOLDOWN, ...)</code>.
     * @param cooldownGroup Cooldown group key as an enum
     * @param secs Cooldown in seconds
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder useCooldown(@NotNull Enum<?> cooldownGroup, float secs) {
        return useCooldown(Core.keys.get(cooldownGroup), secs);
    }

    /**
     * Attaches this item to a cooldown group, identified by a String.
     * When any item in a cooldown group is used, the cooldown is applied to all items in the group.
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.USE_COOLDOWN, ...)</code>.
     * @param cooldownGroup Cooldown group key as a string
     * @param secs Cooldown in seconds
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    public ItemBuilder useCooldown(@NotNull String cooldownGroup, float secs) {
        return useCooldown(Core.keys.get(cooldownGroup), secs);
    }

    /**
     * Sets the used remainder of the item.
     * When the item is used, it will be converted into the remainder item (e.g. milk bucket -> empty bucket).
     * <p>
     * This method is a shortcut method for <code>component(DataComponentTypes.USE_REMAINDER, ...)</code>.
     * @param remainder Remainder item, or null to reset the component
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder useRemainder(@Nullable ItemStack remainder) {
        if (remainder == null)
            return resetComponent(DataComponentTypes.USE_REMAINDER);
        return component(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(remainder));
    }

    /**
     * Adds a 1.21+ data component to the item using a builder.
     * <p>
     * <i>Considered unstable API due to the underlying unstable Paper implementation.</i>
     * @apiNote
     * You can alternatively add some components through other stable API methods,
     * but in exchange for stability, many only support a limited subset of properties.
     * This method, however, will always encompass all components and properties supported by Paper.
     * @param type Data component type
     * @param builder Data component builder
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public <T> ItemBuilder component(DataComponentType.Valued<T> type, DataComponentBuilder<T> builder) {
        item.setData(type, builder);
        return this;
    }

    /**
     * Adds a 1.21+ data component to the item using a value.
     * <p>
     * <i>Considered unstable API due to the underlying unstable Paper implementation.</i>
     * @apiNote
     * You can alternatively add some components through other stable API methods,
     * but in exchange for stability, many only support a limited subset of properties.
     * This method, however, will always encompass all components and properties supported by Paper.
     * @param type Data component type
     * @param value Data component value
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public <T> ItemBuilder component(DataComponentType.Valued<T> type, T value) {
        item.setData(type, value);
        return this;
    }

    /**
     * Adds a 1.21+ data component to the item as a flag.
     * <p>
     * <i>Considered unstable API due to the underlying unstable Paper implementation.</i>
     * @apiNote
     * You can alternatively add some components through other stable API methods,
     * but in exchange for stability, many only support a limited subset of properties.
     * This method, however, will always encompass all components and properties supported by Paper.
     * @param type Data component type
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder component(DataComponentType.NonValued type) {
        item.setData(type);
        return this;
    }

    /**
     * Removes a 1.21+ data component from the item.
     * <p>
     * <i>Considered unstable API due to the underlying unstable Paper implementation.</i>
     * @param type Data component type
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder removeComponent(DataComponentType type) {
        item.unsetData(type);
        return this;
    }

    /**
     * Resets a 1.21+ data component on the item to the item's default value for that component.
     * <p>
     * <i>Considered unstable API due to the underlying unstable Paper implementation.</i>
     * @param type Data component type
     * @return This
     * @since 0.0.17
     */
    @PublicAPI
    @ApiStatus.Experimental
    @SuppressWarnings("UnstableApiUsage")
    public ItemBuilder resetComponent(DataComponentType type) {
        item.resetData(type);
        return this;
    }

    /**
     * Sets a persistent data value with the given key on the item.
     * @param key Key (NamespacedKey)
     * @param type Persistent data type
     * @param value Value
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public <P, C> ItemBuilder data(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        return meta(m -> m.getPersistentDataContainer().set(key, type, value));
    }

    /**
     * Sets a persistent data value with the given key on the item.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param key Key (String)
     * @param type Persistent data type
     * @param value Value
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public <P, C> ItemBuilder data(String key, PersistentDataType<P, C> type, C value) {
        return data(Core.keys.get(key), type, value);
    }

    /**
     * Sets a persistent data value with the given key on the item.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param key Key (Enum)
     * @param type Persistent data type
     * @param value Value
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public <P, C> ItemBuilder data(Enum<?> key, PersistentDataType<P, C> type, C value) {
        return data(Core.keys.get(key), type, value);
    }

    /**
     * Directly edits the item's meta.
     * Useful for functionality not covered by this item builder.
     * @param consumer ItemMeta consumer
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public ItemBuilder meta(Consumer<ItemMeta> consumer) {
        item.editMeta(consumer);
        return this;
    }

    /**
     * Directly edits the item's meta using a specific ItemMeta subclass.
     * Useful for functionality not covered by this item builder.
     * @param metaClass ItemMeta subclass
     * @param consumer ItemMeta consumer
     * @return This
     * @since 0.0.15
     */
    @PublicAPI
    public <MetaType extends ItemMeta> ItemBuilder meta(Class<MetaType> metaClass, Consumer<MetaType> consumer) {
        item.editMeta(metaClass, consumer);
        return this;
    }

    private TextComponent nonItalicWithItalicSupport(String text) {
        return nonItalicWithItalicSupport(text, new Object[0]);
    }

    private TextComponent nonItalicWithItalicSupport(String text, Object[] placeholders) {
        return Component.text("")
            .decoration(TextDecoration.ITALIC, false)
            .append(Core.text.from(text, placeholders));
    }

    private TextComponent nonItalicWithItalicSupport(String text, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        return Component.text("")
            .decoration(TextDecoration.ITALIC, false)
            .append(Core.text.from(text, placeholderResolver));
    }

    /**
     * Builds the item.
     * Always returns a completely new (cloned) ItemStack instance.
     * @return Built item
     * @since 0.0.15
     */
    @PublicAPI
    @Contract(pure = true)
    public ItemStack build() {
        return item.clone();
    }

}
