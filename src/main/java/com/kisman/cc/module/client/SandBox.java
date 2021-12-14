package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class SandBox extends Module {
    public static SandBox instance;

    public SandBox() {
        super("SandBoxTest", "this, ", Category.CLIENT);
        setToggled(true);

        instance = this;
    }
}
