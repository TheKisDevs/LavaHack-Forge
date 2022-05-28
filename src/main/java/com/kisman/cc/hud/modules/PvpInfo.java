package com.kisman.cc.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.HudModule;
import com.kisman.cc.module.combat.*;
import com.kisman.cc.module.movement.Speed;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.*;

public class PvpInfo extends HudModule {
    private final Setting offsets = register(new Setting("Offsets", this, 2, 0, 10, true));

    public PvpInfo() {
        super("PvpInfo", "PvpInfo", true);

        setX(1);
        setY(1);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRender(RenderGameOverlayEvent.Text event) {
        int y = (int) getY();
        int height = offsets.getValInt() + CustomFontUtil.getFontHeight();
        int count = 0;
        setW(CustomFontUtil.getStringWidth("Speed: OFF"));

        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "ReR: " + (AutoRer.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "KA: " + (KillAura.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "FA: " + (AutoFirework.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "OFF: " + (OffHand.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "HR: " + (Kisman.instance.moduleManager.getModule("HandRewrite").toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "SURR: " + (Surround.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "AT: " + (Surround.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "HFR: " + (Surround.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "CF: " + (CrystalFiller.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;
        CustomFontUtil.drawStringWithShadow(TextFormatting.GRAY + "Speed: " + (Speed.instance.toggled ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF"), getX(), count * height + y, -1);
        count++;

        setH(count * height);
    }
}
