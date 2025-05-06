package com.elvenide.core.providers.item;

import com.elvenide.core.Core;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ItemBuilder {

    private final ItemStack item;

    @ApiStatus.Internal
    public ItemBuilder(Core instance, @Nullable Material material) {
        if (instance == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");

        if (material == null)
            throw new IllegalArgumentException("Material cannot be null");

        this.item = ItemStack.of(material, 1);
    }

    @ApiStatus.Internal
    public ItemBuilder(Core instance, @NotNull ItemStack item) {
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
    public ItemBuilder name(@NotNull String name, Object... placeholders) {
        item.editMeta(m -> m.customName(nonItalicWithItalicSupport(name, placeholders)));
        return this;
    }

    /**
     * Sets the name of the item, with support for a third-party placeholder plugin.
     * @param name String name
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder name(@NotNull String name, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        item.editMeta(m -> m.customName(nonItalicWithItalicSupport(name, placeholderResolver)));
        return this;
    }

    /**
     * Adds a line of lore of the item, with optional placeholders.
     * @param loreLine String lore line
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder lore(@NotNull String loreLine, Object... placeholders) {
        item.editMeta(m -> {
            List<Component> existingLore = m.lore();
            ArrayList<Component> lore = existingLore == null ? new ArrayList<>() : new ArrayList<>(existingLore);
            lore.add(nonItalicWithItalicSupport(loreLine, placeholders));
            m.lore(lore);
        });
        return this;
    }

    /**
     * Adds a line of lore of the item, with support for a third-party placeholder plugin.
     * @param loreLine String lore line
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder lore(@NotNull String loreLine, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        item.editMeta(m -> {
            List<Component> existingLore = m.lore();
            ArrayList<Component> lore = existingLore == null ? new ArrayList<>() : new ArrayList<>(existingLore);
            lore.add(nonItalicWithItalicSupport(loreLine, placeholderResolver));
            m.lore(lore);
        });
        return this;
    }

    /**
     * Sets the lore of the item.
     * @param lore List of lore lines
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder lores(@NotNull ArrayList<String> lore) {
        item.editMeta(m -> m.lore(lore.stream().map(this::nonItalicWithItalicSupport).toList()));
        return this;
    }

    /**
     * Sets the lore of the item, with support for a third-party placeholder plugin.
     * @param lore List of lore lines
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder lores(@NotNull ArrayList<String> lore, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        item.editMeta(m -> m.lore(lore.stream().map(s -> nonItalicWithItalicSupport(s, placeholderResolver)).toList()));
        return this;
    }

    /**
     * Sets the max stack size of the item.
     * @param size Max stack size
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder maxStackSize(int size) {
        if (size < 1)
            throw new IllegalArgumentException("Max stack size cannot be less than 1");

        item.editMeta(m -> {
            m.setMaxStackSize(size);
        });
        return this;
    }

    /**
     * Sets the owner of the player head item.
     * Silently fails if the item is not a player head.
     * @param name Player name
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder headOwner(String name) {
        item.editMeta(SkullMeta.class, m -> {
            m.setPlayerProfile(Bukkit.createProfile(name));
        });
        return this;
    }

    /**
     * Sets the color of the leather armor item.
     * Silently fails if the item is not leather armor.
     * @param color Color
     * @return This
     * @since 0.0.15
     */
    public ItemBuilder color(Color color) {
        item.editMeta(LeatherArmorMeta.class, m -> {
            m.setColor(color);
        });
        return this;
    }

    /**
     * Adds a 1.21+ data component to the item.
     * @param type Data component type
     * @param builder Data component builder
     * @return This
     * @since 0.0.15
     */
    public <T> ItemBuilder component(DataComponentType.Valued<T> type, DataComponentBuilder<T> builder) {
        item.setData(type, builder);
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
    public <P, C> ItemBuilder data(NamespacedKey key, PersistentDataType<P, C> type, C value) {
        item.editMeta(m -> {
            m.getPersistentDataContainer().set(key, type, value);
        });
        return this;
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
            .append(Core.text.deserialize(text, placeholders));
    }

    private TextComponent nonItalicWithItalicSupport(String text, BiFunction<@Nullable Player, @NotNull String, @NotNull String> placeholderResolver) {
        return Component.text("")
            .decoration(TextDecoration.ITALIC, false)
            .append(Core.text.deserialize(text, placeholderResolver));
    }

    /**
     * Builds the item.
     * Always returns a completely new ItemStack instance.
     * @return Built item
     * @since 0.0.15
     */
    public ItemStack build() {
        return item.clone();
    }

}
