package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class SkyColor extends Module {
    private float[] color;

    public SkyColor() {
        super("SkyColor", "colorsky", Category.RENDER);

        this.color = new float[] {1, 1, 1, 1};

        Kisman.instance.settingsManager.rSetting(new Setting("Color", this, "Color", new float[] {1, 1, 1, 1}, false));
    }

    public void update() {
        this.color[0] = Kisman.instance.settingsManager.getSettingByName(this, "Color").getColor(0);
        this.color[1] = Kisman.instance.settingsManager.getSettingByName(this, "Color").getColor(1);
        this.color[2] = Kisman.instance.settingsManager.getSettingByName(this, "Color").getColor(2);
        this.color[3] = Kisman.instance.settingsManager.getSettingByName(this, "Color").getColor(3);
    }

    @SubscribeEvent
    public void fogColor(EntityViewRenderEvent.FogColors event) {
        event.setRed(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])).getRed());
        event.setGreen(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])).getGreen());
        event.setBlue(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])).getBlue());
    }

    @SubscribeEvent
    public void fog(EntityViewRenderEvent.FogDensity event) {
        event.setDensity(0);
        event.setCanceled(false);
    }
}
