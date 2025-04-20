package com.elvenide.core.providers.text;

import com.elvenide.core.providers.TextProvider;
import org.jetbrains.annotations.ApiStatus;

public class MoreColorsPackage implements TextPackageSupplier {

    @ApiStatus.Internal
    @Override
    public void build(TextProvider textProvider) {
        textProvider.addColorTag("brown", "#94570d");
        textProvider.addColorTag("orange", "#ff8000");
        textProvider.addColorTag("orange_red", "#e63900");
        textProvider.addColorTag("smooth_purple", "#ab73ff");
        textProvider.addColorTag("indigo", "#3900e6");
        textProvider.addColorTag("smooth_blue", "#009aed");
    }

}
