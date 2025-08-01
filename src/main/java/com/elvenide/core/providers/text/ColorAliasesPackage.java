package com.elvenide.core.providers.text;

import org.jetbrains.annotations.ApiStatus;

class ColorAliasesPackage implements TextPackageSupplier {

    @ApiStatus.Internal
    @Override
    public void build(TextProvider textProvider) {
        textProvider.addColorTag("pink", "#ff55ff");
        textProvider.addColorTag("purple", "#aa00aa");
        textProvider.addColorTag("cyan", "#00aaaa");
        textProvider.addColorTag("light_blue", "#5555ff");
    }

}
