package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class HandCharms extends Module {
    public static HandCharms instance;

    public HandCharms() {
        super("HandCHarms", "", Category.RENDER);

        instance = this;
    }
}
