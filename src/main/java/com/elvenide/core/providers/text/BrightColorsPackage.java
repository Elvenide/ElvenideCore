package com.elvenide.core.providers.text;

import com.elvenide.core.providers.map.CoreMap;

final class BrightColorsPackage implements TextPackageSupplier {
    @Override
    public CoreMap<String, String> getColorTags() {
        return CoreMap.of("bright_red", "#ff0000")
            .add("bright_blue", "#0000ff")
            .add("bright_pink", "#ff00ff")
            .add("bright_yellow", "#ffff00");
    }
}
