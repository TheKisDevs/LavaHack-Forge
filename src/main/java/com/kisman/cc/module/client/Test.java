package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.entity.item.EntityEnderCrystal;

public class Test extends Module {
    public Test() {
        super("Test", "", Category.CLIENT);
        setmgr.rSetting(new Setting("ExampleEntityPreview", this, "Example", new EntityEnderCrystal(mc.world)));
    }
}
