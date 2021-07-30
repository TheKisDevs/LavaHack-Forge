package com.kisman.cc.clickgui;

import com.kisman.cc.Kisman;
import com.kisman.cc.oldclickgui.component.Component;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class NewClickGUI extends GuiScreen {

    public NewClickGUI() {
        Kisman.LOGGER.info("1");
        Frame frame = new Frame(Kisman.NAME);
        frame.renderFrame(fontRenderer);
        Kisman.LOGGER.info("2");
    }

    @Override
    public void initGui() {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {}

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {}

    @Override
    protected void keyTyped(char typedChar, int keyCode) {}


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {}
}
