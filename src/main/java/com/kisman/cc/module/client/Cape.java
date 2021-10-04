package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class Cape extends Module {
    public static Cape instance;

    public Cape() {
        super("Cape", "Custom cape", Category.CLIENT);

        instance = this;
    }
}
