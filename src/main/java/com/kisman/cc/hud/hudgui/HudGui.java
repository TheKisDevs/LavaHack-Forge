package com.kisman.cc.hud.hudgui;

import com.kisman.cc.hud.hudgui.frame.Frame;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public class HudGui extends GuiScreen {
    public static ArrayList<Frame> frames;

    public HudGui() {
        this.frames = new ArrayList<>();
        Frame frame = new Frame();
        frames.add(frame);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //frame.renderFrame(fontRenderer);
        for(Frame frame : frames) {
            frame.renderFrame(this.fontRenderer);
        }
    }

//    @Override
//    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
//    }
//
//    @Override
//    protected void keyTyped(char typedChar, int keyCode) {
//    }
//
//
//    @Override
//    protected void mouseReleased(int mouseX, int mouseY, int state) {
//    }
//
//    @Override
//    public boolean doesGuiPauseGame() {
//        return true;
//    }
}
