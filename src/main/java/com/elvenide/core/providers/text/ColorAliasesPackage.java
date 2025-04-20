package com.elvenide.core.providers.text;

import com.elvenide.core.providers.TextProvider;
import org.jetbrains.annotations.ApiStatus;

public class ColorAliasesPackage implements TextPackageSupplier {

    @ApiStatus.Internal
    @Override
    public void build(TextProvider textProvider) {
        textProvider.addColorTag("pink", "#ff55ff");
        textProvider.addColorTag("purple", "#aa00aa");
        textProvider.addColorTag("cyan", "#00aaaa");
        textProvider.addColorTag("light_blue", "#5555ff");
    }

}
