package com.elvenide.core.providers.text;

import com.elvenide.core.providers.TextProvider;
import org.jetbrains.annotations.ApiStatus;

public class BrightColorsPackage implements TextPackageSupplier {

    @ApiStatus.Internal
    @Override
    public void build(TextProvider textProvider) {
        textProvider.addColorTag("bright_red", "#ff0000");
        textProvider.addColorTag("bright_blue", "#0000ff");
        textProvider.addColorTag("bright_pink", "#ff00ff");
        textProvider.addColorTag("bright_yellow", "#ffff00");
    }

}
