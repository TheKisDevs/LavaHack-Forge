package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.*;

public class PvpInfo extends HudModule {
    public PvpInfo() {
        super("PvpInfo", "PvpInfo", HudCategory.COMBAT);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRender(RenderGameOverlayEvent.Text event) {
        int y = (int) HUD.instance.pvpY.getValDouble();
        int heigth = 2 + CustomFontUtil.getFontHeight();
        int count = 0;

        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "KA: " + (isToggled(KillAura.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "CA: " + (isToggled(AutoCrystalRewrite.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "FA: " + (isToggled(AutoFirework.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "OFF: " + (isToggled(OffHand.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "SURR: " + (isToggled(Surround.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "AT: " + (isToggled(AutoTrap.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "HF: " + (isToggled(HoleFiller.instance) ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), 1, count * heigth + y, -1);
    }

    private boolean isToggled(Module mod) {
        return mod.isToggled();
    }
}
