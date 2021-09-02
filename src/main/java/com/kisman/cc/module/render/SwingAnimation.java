package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

public class SwingAnimation extends Module {
    private ArrayList<String> swingMode = new ArrayList<>(Arrays.asList("1", "2", "3"));

    private String swingModeString;

    public SwingAnimation() {
        super("SwingAnimation", "SwingAnimation", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("SwingMode", this, "1", swingMode));
    }

    public void update() {
        this.swingModeString = Kisman.instance.settingsManager.getSettingByName(this, "SwingMode").getValString();
    }

    @SubscribeEvent
    public void onRenderArms(final RenderSpecificHandEvent event) {
        if (event.getSwingProgress() > 0) {
            final float angle = (1f - event.getSwingProgress()) * 360f;

            switch (swingModeString) {
                case "1":
                    glRotatef(angle, 1, 0, 0);
                    break;
                case "2":
                    glRotatef(angle, 0, 1, 0);
                    break;
                case "3":
                    glRotatef(angle, 0, 0, 1);
                    break;
            }
        }
    }
}
