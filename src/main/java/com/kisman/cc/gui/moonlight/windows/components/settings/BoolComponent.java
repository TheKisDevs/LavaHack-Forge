package com.kisman.cc.gui.moonlight.windows.components.settings;

import com.kisman.cc.gui.moonlight.windows.components.api.SettingComponent;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public final class BoolComponent extends SettingComponent {
    private final Setting set;

    public BoolComponent(Setting set, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.set = set;
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0) {
            set.setValBoolean(!set.getValBoolean());
        }
    }

    @Override
    public void release(int mouseX, int mouseY, int mouseButton) { }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (isInside(mouseX, mouseY)) {
            Gui.drawRect(x, y, x + width, y + height, 0x20ffffff);
        }

        final StringBuilder sb = new StringBuilder(set.getName() + ":");

        if (set.getValBoolean()) {
            sb.append(ChatFormatting.GREEN + " [True]");
        } else {
            sb.append(ChatFormatting.RED + " [False]");
        }

        CustomFontUtil.drawStringWithShadow(sb.toString(), x + 2f, y + (height / 2f) - (CustomFontUtil.getFontHeight() / 2f) - 1f, -1);
    }

    @Override
    public void typed(char keyChar, int keyCode) {
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
