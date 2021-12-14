package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class ShulkerPreview extends Module {
    public static ShulkerPreview instance;

    public ShulkerPreview() {
        super("ShulkerPreview", "ShulkerPreview", Category.RENDER);

        instance = this;
    }
}
