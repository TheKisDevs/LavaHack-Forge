package com.kisman.cc.oldclickgui.component.components.sub.sub;

import net.minecraft.client.Minecraft;

public class LineButton extends SubComponent{
    int count = 0;

    @Override
    public void renderComponent() {
        Minecraft.getMinecraft().player.sendChatMessage("SubLine renderer! " + count);
        count++;
    }
}
