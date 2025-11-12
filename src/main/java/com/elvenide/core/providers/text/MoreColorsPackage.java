package com.elvenide.core.providers.text;

import com.elvenide.core.providers.map.CoreMap;

final class MoreColorsPackage implements TextPackageSupplier {
    @Override
    public CoreMap<String, String> getColorTags() {
        return CoreMap.of("brown", "#94570d")
            .add("orange", "#ff8000")
            .add("orange_red", "#e63900")
            .add("smooth_purple", "#ab73ff")
            .add("indigo", "#3900e6")
            .add("smooth_blue", "#009aed");
    }
}
