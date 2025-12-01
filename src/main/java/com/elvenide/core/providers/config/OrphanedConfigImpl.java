package com.elvenide.core.providers.config;

import org.jetbrains.annotations.NotNull;

import java.io.File;

class OrphanedConfigImpl extends Config {

    /// This constructor has been made private, as MockConfigs do not have a file
    @SuppressWarnings("unused")
    private OrphanedConfigImpl(File file) {
        super(file);
    }

    /// This constructor has been made private, as MockConfigs do not have a file or resource
    @SuppressWarnings("unused")
    private OrphanedConfigImpl(File file, @NotNull String resource) {
        super(file, resource);
    }

    OrphanedConfigImpl() {
        super();
    }

    @Override
    public void reload() {
        throw new IllegalStateException("Orphaned config sections cannot be reloaded.");
    }

    @Override
    public void save() {
        throw new IllegalStateException("Orphaned config sections cannot be saved.");
    }

    @Override
    public String getFileName() {
        throw new IllegalStateException("Orphaned config sections do not have a file name.");
    }
}
