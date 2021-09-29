package com.kisman.cc.newclickgui;

import com.kisman.cc.newclickgui.component.Frame;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class NewGui extends GuiScreen {
    private Frame frame;

    public NewGui() {
        this.frame = new Frame(1, 1, 250, 400, fontRenderer);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.frame.renderComponent();
        this.frame.updateComponent(mouseX, mouseY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.frame.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.frame.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.frame.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
