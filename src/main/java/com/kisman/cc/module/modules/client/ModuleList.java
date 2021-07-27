package com.kisman.cc.module.modules.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModuleList extends Module {
    public ModuleList() {
        super("ModuleList", "Module List on your screen", Category.CLIENT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent egoe) {
		/*if (!egoe.type.equals(egoe.type.CROSSHAIRS)) {
			return;
		}*/
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int y = 2;
        for (Module mod : Kisman.instance.moduleManager.getModuleList()) {
            if (!mod.getName().equalsIgnoreCase("HUD") && mod.isToggled() && mod.visible) {
                FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
                fr.drawString(mod.getName(), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - 1, y, 0xFFFFFF);
                y += fr.FONT_HEIGHT;
            }
        }
    }
}
