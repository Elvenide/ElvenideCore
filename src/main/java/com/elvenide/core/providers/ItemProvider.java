package com.elvenide.core.providers;

import com.elvenide.core.Core;
import com.elvenide.core.Provider;
import com.elvenide.core.providers.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemProvider extends Provider {

    @ApiStatus.Internal
    public ItemProvider(@Nullable Core core) {
        super(core);
    }

    /**
     * Returns a builder to create a new item with the given material.
     * @param material Material
     * @return Item builder
     * @since 0.0.15
     */
    public ItemBuilder builder(@NotNull Material material) {
        return new ItemBuilder(core, material);
    }

    /**
     * Returns a builder to modify an existing item.
     * The given item will be mutated by the methods on the builder.
     * @param item Item
     * @return Item builder
     * @since 0.0.15
     */
    public ItemBuilder builder(@NotNull ItemStack item) {
        return new ItemBuilder(core, item);
    }

    /**
     * Gets persistent data added to an item by your plugin.
     * @param item Item
     * @param key Data key
     * @param type Data type
     * @param def Default value
     * @return Your data, or the default value if no data was found
     * @param <P> Underlying primitive type
     * @param <C> Data return type
     * @since 0.0.15
     */
    public <P, C> C getData(ItemStack item, NamespacedKey key, PersistentDataType<P, C> type, C def) {
        if (!item.hasItemMeta())
            throw new IllegalArgumentException("Cannot get data on ItemStack that does not have item meta.");
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(key, type, def);
    }

    /**
     * Gets persistent data added to an item by your plugin.
     * @param item Item
     * @param key Data key
     * @param type Data type
     * @return Your data
     * @param <P> Underlying primitive type
     * @param <C> Data return type
     * @since 0.0.15
     */
    public <P, C> C getData(ItemStack item, NamespacedKey key, PersistentDataType<P, C> type) {
        if (!item.hasItemMeta())
            throw new IllegalArgumentException("Cannot get data on ItemStack that does not have item meta.");
        return item.getItemMeta().getPersistentDataContainer().get(key, type);
    }

    /**
     * Gets persistent data added to an item by your plugin.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param item Item
     * @param key Data key
     * @param type Data type
     * @return Your data
     * @param <P> Underlying primitive type
     * @param <C> Data return type
     * @since 0.0.15
     */
    public <P, C> C getData(ItemStack item, String key, PersistentDataType<P, C> type) {
        if (!item.hasItemMeta())
            throw new IllegalArgumentException("Cannot get data on ItemStack that does not have item meta.");
        return item.getItemMeta().getPersistentDataContainer().get(Core.keys.get(key), type);
    }

    /**
     * Gets persistent data added to an item by your plugin.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param item Item
     * @param key Data key
     * @param type Data type
     * @param def Default value
     * @return Your data, or the default value if no data was found
     * @param <P> Underlying primitive type
     * @param <C> Data return type
     * @since 0.0.15
     */
    public <P, C> C getData(ItemStack item, String key, PersistentDataType<P, C> type, C def) {
        if (!item.hasItemMeta())
            throw new IllegalArgumentException("Cannot get data on ItemStack that does not have item meta.");
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(Core.keys.get(key), type, def);
    }

    /**
     * Gets persistent data added to an item by your plugin.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param item Item
     * @param key Data key
     * @param type Data type
     * @return Your data
     * @param <P> Underlying primitive type
     * @param <C> Data return type
     * @since 0.0.15
     */
    public <P, C> C getData(ItemStack item, Enum<?> key, PersistentDataType<P, C> type) {
        if (!item.hasItemMeta())
            throw new IllegalArgumentException("Cannot get data on ItemStack that does not have item meta.");
        return item.getItemMeta().getPersistentDataContainer().get(Core.keys.get(key), type);
    }

    /**
     * Gets persistent data added to an item by your plugin.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param item Item
     * @param key Data key
     * @param type Data type
     * @param def Default value
     * @return Your data, or the default value if no data was found
     * @param <P> Underlying primitive type
     * @param <C> Data return type
     * @since 0.0.15
     */
    public <P, C> C getData(ItemStack item, Enum<?> key, PersistentDataType<P, C> type, C def) {
        if (!item.hasItemMeta())
            throw new IllegalArgumentException("Cannot get data on ItemStack that does not have item meta.");
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(Core.keys.get(key), type, def);
    }

    /**
     * Checks if an item has persistent data added by your plugin.
     * @param item Item
     * @param key Data key
     * @return Whether the item has your data
     * @since 0.0.15
     */
    public boolean hasData(ItemStack item, NamespacedKey key) {
        if (!item.hasItemMeta())
            return false;
        return item.getItemMeta().getPersistentDataContainer().has(key);
    }

    /**
     * Checks if an item has persistent data added by your plugin.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param item Item
     * @param key Data key
     * @return Whether the item has your data
     * @since 0.0.15
     */
    public boolean hasData(ItemStack item, String key) {
        if (!item.hasItemMeta())
            return false;
        return item.getItemMeta().getPersistentDataContainer().has(Core.keys.get(key));
    }

    /**
     * Checks if an item has persistent data added by your plugin.
     * Automatically converts the key to a NamespacedKey using {@link Core#keys}.
     * @param item Item
     * @param key Data key
     * @return Whether the item has your data
     * @since 0.0.15
     */
    public boolean hasData(ItemStack item, Enum<?> key) {
        if (!item.hasItemMeta())
            return false;
        return item.getItemMeta().getPersistentDataContainer().has(Core.keys.get(key));
    }

}
