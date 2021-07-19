package com.kisman.cc.gui;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen {
    public ArrayList<Frame> frames;

    public ClickGui() {
        int offset = 0;
        frames = new ArrayList<>();
        for(Category c : Category.values()) {
            Frame frame = new Frame(c.name(), 100 + offset, 20 ,100, 12);
            for(Module m : Kisman.instance.moduleManager.getModsInCategory(c)) {
                Button button = new Button(frame, m);
                frame.buttons.add(button);
            }
            frames.add(frame);
            offset += 150;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        for(Frame f : frames) {
            f.update(mouseX, mouseY);
            f.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for(Frame f : frames) {
            f.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(Frame f : frames) {
            f.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for(Frame f : frames) {}
    }

    @Override
    public boolean doesGuiPauseGame() {
        return super.doesGuiPauseGame();
    }
}
