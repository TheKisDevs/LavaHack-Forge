package com.kisman.cc.oldclickgui;

import com.kisman.cc.oldclickgui.block.Frame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class BlockGui extends GuiScreen {
    public Frame frame;

    public int x;
    public int y;

    public int heigth = 50;
    public int width = 13;

    public BlockGui() {
        x = Minecraft.getMinecraft().displayWidth;
        y = Minecraft.getMinecraft().displayWidth;

        this.frame = new Frame("Example", x, y, heigth, width);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        frame.renderFrame(fontRenderer);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void initGui() {
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
