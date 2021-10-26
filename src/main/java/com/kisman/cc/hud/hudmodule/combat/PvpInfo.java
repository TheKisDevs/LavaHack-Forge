package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.combat.AutoCrystal;
import com.kisman.cc.module.combat.AutoFirework;
import com.kisman.cc.module.combat.KillAura;
import com.kisman.cc.module.combat.OffHand;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PvpInfo extends HudModule {
    public PvpInfo() {
        super("PvpInfo", "PvpInfo", HudCategory.COMBAT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        int y = (int) HUD.instance.pvpY.getValDouble();
        int heigth = 2 + CustomFontUtil.getFontHeight();
        int count = 0;

        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "KA: " + (isToggled(KillAura.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "CA: " + (isToggled(AutoCrystal.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "FA: " + (isToggled(AutoFirework.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "OFF: " + (isToggled(OffHand.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
    }

    private boolean isToggled(Module mod) {
        return mod.isToggled();
    }
}
