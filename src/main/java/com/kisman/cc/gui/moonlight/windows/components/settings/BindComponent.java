package com.kisman.cc.gui.moonlight.windows.components.settings;

import com.kisman.cc.module.Module;
import com.kisman.cc.gui.moonlight.windows.components.api.SettingComponent;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public final class BindComponent extends SettingComponent {
    private boolean listening;
    private Module mod;

    public BindComponent(Module mod, int x, int y, int width, int height) {
        super(x, y, width, height);

        this.mod = mod;
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0) {
            listening = !listening;
        }
    }

    @Override
    public void release(int mouseX, int mouseY, int mouseButton) { }

    @Override
    public void draw(int mouseX, int mouseY, float partialTicks) {
        if (isInside(mouseX, mouseY)) {
            Gui.drawRect(x, y, x + width, y + height, 0x20ffffff);
        }
        final String text = listening ? "Press a key..." : "Keybind [" + Keyboard.getKeyName(mod.getKey()) + "]";

        CustomFontUtil.drawStringWithShadow(text, x + 2f, y + (height / 2f) - (CustomFontUtil.getFontHeight() / 2f) - 1f, -1);
    }

    @Override
    public void typed(char keyChar, int keyCode) {
        if (listening) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                mod.setKey(0);
            } else {
                mod.setKey(keyCode);
            }

            listening = false;
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
