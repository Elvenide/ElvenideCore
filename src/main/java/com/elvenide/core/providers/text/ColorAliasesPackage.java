package com.elvenide.core.providers.text;

import com.elvenide.core.providers.map.CoreMap;
import org.jetbrains.annotations.NotNull;

final class ColorAliasesPackage implements TextPackageSupplier {
    @Override
    public @NotNull CoreMap<String, String> getColorTags() {
        return CoreMap.of("pink", "#ff55ff")
            .add("purple", "#aa00aa")
            .add("cyan", "#00aaaa")
            .add("light_blue", "#5555ff");
    }

    @Override
    public @NotNull CoreMap<String, String> getTextTags() {
        return new CoreMap<>();
    }
}
