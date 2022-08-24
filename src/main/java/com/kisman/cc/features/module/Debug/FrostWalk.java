package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

public class FrostWalk extends Module {

    public final Setting level = register(new Setting("Level", this, 2, 1, 100, true));

    public static FrostWalk INSTANCE;

    public FrostWalk(){
        super("FrostWalk", Category.DEBUG);
        INSTANCE = this;
    }
}
