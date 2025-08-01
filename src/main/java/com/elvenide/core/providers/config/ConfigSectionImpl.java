package com.elvenide.core.providers.config;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

record ConfigSectionImpl(@NotNull ConfigurationSection section, @Nullable ConfigSection parent,
                         @NotNull Config root) implements ConfigSection {

    @Override
    public @NotNull Set<String> getKeys(boolean deep) {
        return section.getKeys(deep);
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        return section.getValues(deep);
    }

    @Override
    public boolean contains(@NotNull String path) {
        return section.contains(path);
    }

    @Override
    public boolean contains(@NotNull String path, boolean ignoreDefault) {
        return section.contains(path, ignoreDefault);
    }

    @Override
    public boolean isSet(@NotNull String path) {
        return section.isSet(path);
    }

    @Override
    public @Nullable String getCurrentPath() {
        return section.getCurrentPath();
    }

    @Override
    public @NotNull String getName() {
        return section.getName();
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        return section.get(path);
    }

    @Override
    public @Nullable Object get(@NotNull String path, @Nullable Object def) {
        return section.get(path, def);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        section.set(path, value);
    }

    @Override
    public @Nullable String getString(@NotNull String path) {
        return section.getString(path);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return section.getString(path, def);
    }

    @Override
    public boolean isString(@NotNull String path) {
        return section.isString(path);
    }

    @Override
    public int getInt(@NotNull String path) {
        return section.getInt(path);
    }

    @Override
    public int getInt(@NotNull String path, int def) {
        return section.getInt(path, def);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return section.isInt(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return section.getBoolean(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path, boolean def) {
        return section.getBoolean(path, def);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        return section.isBoolean(path);
    }

    @Override
    public double getDouble(@NotNull String path) {
        return section.getDouble(path);
    }

    @Override
    public double getDouble(@NotNull String path, double def) {
        return section.getDouble(path, def);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return section.isDouble(path);
    }

    @Override
    public long getLong(@NotNull String path) {
        return section.getLong(path);
    }

    @Override
    public long getLong(@NotNull String path, long def) {
        return section.getLong(path, def);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return section.isLong(path);
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path) {
        return section.getList(path);
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path, @Nullable List<?> def) {
        return section.getList(path, def);
    }

    @Override
    public boolean isList(@NotNull String path) {
        return section.isList(path);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        return section.getStringList(path);
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        return section.getIntegerList(path);
    }

    @Override
    public @NotNull List<Boolean> getBooleanList(@NotNull String path) {
        return section.getBooleanList(path);
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        return section.getDoubleList(path);
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        return section.getFloatList(path);
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        return section.getLongList(path);
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        return section.getByteList(path);
    }

    @Override
    public @NotNull List<Character> getCharacterList(@NotNull String path) {
        return section.getCharacterList(path);
    }

    @Override
    public @NotNull List<Short> getShortList(@NotNull String path) {
        return section.getShortList(path);
    }

    @Override
    public @NotNull List<Map<?, ?>> getMapList(@NotNull String path) {
        return section.getMapList(path);
    }

    @Override
    public <T> @Nullable T getObject(@NotNull String path, @NotNull Class<T> clazz) {
        return section.getObject(path, clazz);
    }

    @Override
    public <T> @Nullable T getObject(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return section.getObject(path, clazz, def);
    }

    @Override
    public <T extends ConfigurationSerializable> @Nullable T getSerializable(@NotNull String path, @NotNull Class<T> clazz) {
        return section.getSerializable(path, clazz);
    }

    @Override
    public <T extends ConfigurationSerializable> @Nullable T getSerializable(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return section.getSerializable(path, clazz, def);
    }

    @Override
    public @Nullable Vector getVector(@NotNull String path) {
        return section.getVector(path);
    }

    @Override
    public @Nullable Vector getVector(@NotNull String path, @Nullable Vector def) {
        return section.getVector(path, def);
    }

    @Override
    public boolean isVector(@NotNull String path) {
        return section.isVector(path);
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(@NotNull String path) {
        return section.getOfflinePlayer(path);
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(@NotNull String path, @Nullable OfflinePlayer def) {
        return section.getOfflinePlayer(path, def);
    }

    @Override
    public boolean isOfflinePlayer(@NotNull String path) {
        return section.isOfflinePlayer(path);
    }

    @Override
    public @Nullable ItemStack getItemStack(@NotNull String path) {
        return section.getItemStack(path);
    }

    @Override
    public @Nullable ItemStack getItemStack(@NotNull String path, @Nullable ItemStack def) {
        return section.getItemStack(path, def);
    }

    @Override
    public boolean isItemStack(@NotNull String path) {
        return section.isItemStack(path);
    }

    @Override
    public @Nullable Color getColor(@NotNull String path) {
        return section.getColor(path);
    }

    @Override
    public @Nullable Color getColor(@NotNull String path, @Nullable Color def) {
        return section.getColor(path, def);
    }

    @Override
    public boolean isColor(@NotNull String path) {
        return section.isColor(path);
    }

    @Override
    public @Nullable Location getLocation(@NotNull String path) {
        return section.getLocation(path);
    }

    @Override
    public @Nullable Location getLocation(@NotNull String path, @Nullable Location def) {
        return section.getLocation(path, def);
    }

    @Override
    public boolean isLocation(@NotNull String path) {
        return section.isLocation(path);
    }

    @Override
    public boolean isConfigurationSection(@NotNull String path) {
        return section.isConfigurationSection(path);
    }

    @Override
    public @Nullable ConfigurationSection getDefaultSection() {
        return section.getDefaultSection();
    }

    @Override
    public void addDefault(@NotNull String path, @Nullable Object value) {
        section.addDefault(path, value);
    }

    @Override
    public @NotNull List<String> getComments(@NotNull String path) {
        return section.getComments(path);
    }

    @Override
    public @NotNull List<String> getInlineComments(@NotNull String path) {
        return section.getInlineComments(path);
    }

    @Override
    public void setComments(@NotNull String path, @Nullable List<String> comments) {
        section.setComments(path, comments);
    }

    @Override
    public void setInlineComments(@NotNull String path, @Nullable List<String> comments) {
        section.setInlineComments(path, comments);
    }

    @Override
    public @NotNull Config getRoot() {
        return root;
    }

    @Override
    public @Nullable ConfigSection getParent() {
        return parent;
    }

    @Override
    public @NotNull ConfigSection createSection(@NotNull String path) {
        return new ConfigSectionImpl(section.createSection(path), this, root);
    }

    @Override
    public @NotNull ConfigSection createSection(@NotNull String path, @NotNull Map<?, ?> map) {
        return new ConfigSectionImpl(section.createSection(path, map), this, root);
    }

    @ApiStatus.Experimental
    @Override
    public @Nullable ConfigSection getConfigurationSection(@NotNull String path) {
        if (isConfigurationSection(path))
            return new ConfigSectionImpl(Objects.requireNonNull(section.getConfigurationSection(path)), this, root);
        return null;
    }
}
