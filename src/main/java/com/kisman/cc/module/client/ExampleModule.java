package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventWorldRender;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;

import java.awt.*;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ExampleModule extends Module {
    public ExampleModule() {
        super("ExampleModule", "example", Category.CLIENT);
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory", this, 1, "ExampleCategory"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCCheckBox", this, 1, "ExampleCCheckBox", false));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCategory1", this, 2, "ExampleCategory1"));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleCLine1", this, "ExampleCLine1", 2));
        Kisman.instance.settingsManager.rSetting(new Setting("ExampleColorPicker", this));
    }

    // @SubscribeEvent
    // public void onRender(EventWorldRender event) {
    //     RenderUtil.drawLine(1, 1, 100, 100, 10f, 0xFFFFFF);
    // }
}
