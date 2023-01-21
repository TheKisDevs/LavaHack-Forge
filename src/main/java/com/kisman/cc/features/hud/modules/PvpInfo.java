package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.features.module.combat.*;
import com.kisman.cc.features.module.movement.Speed;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;

public class PvpInfo extends ShaderableHudModule {
    private final Setting offsets = register(new Setting("Offsets", this, 2, 0, 10, true));

    public PvpInfo() {
        super("PvpInfo", "PvpInfo", true, false, false);
    }

    public void handleRender() {
        int y = (int) getY();
        int height = offsets.getValInt() + CustomFontUtil.getFontHeight();
        setW(CustomFontUtil.getStringWidth("SURRr: OFF"));

        shaderRender = () -> {
            int count = 0;
            drawStringWithShadow(TextFormatting.GRAY + "ReR: " + (AutoRer.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "KAr: " + (Kisman.instance.moduleManager.getModule("KillAuraRewrite").toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "FA: " + (AutoFirework.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "OFF: " + (Kisman.instance.moduleManager.getModule("OffHand").toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "Hr: " + (Kisman.instance.moduleManager.getModule("HandRewrite").toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "SURRr: " + (Kisman.instance.moduleManager.getModule("SurroundRewrite").toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "AT: " + (AutoTrap.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "HFr: " + (HoleFillerRewrite.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "FLTNr: " + (FlattenRewrite.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "CF: " + (CrystalFiller.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
            count++;
            drawStringWithShadow(TextFormatting.GRAY + "Speed: " + (Speed.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
//            count++;
        };

        setH(11 * height);
    }
}
