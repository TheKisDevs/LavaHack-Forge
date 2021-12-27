package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class ViewModelRewrite extends Module {
    private Setting posX = new Setting("PosX", this, 0, -4, 4, false);
    private Setting posY = new Setting("PosY", this, 0, -4, 4, false);
    private Setting posZ = new Setting("PosZ", this, 0, -4, 4, false);

    public ViewModelRewrite() {
        super("ViewModelRewrite", Category.RENDER);
    }
}
