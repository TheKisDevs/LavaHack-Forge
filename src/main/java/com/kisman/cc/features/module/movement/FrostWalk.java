package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Beta;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.OnlyDebug;
import com.kisman.cc.settings.Setting;

public class FrostWalk extends Module {

    public final Setting level = register(new Setting("Level", this, 2, 1, 100, true));

    public static FrostWalk INSTANCE;

    public FrostWalk(){
        super("FrostWalk", Category.MISC);
        INSTANCE = this;
    }
}
