package com.kisman.cc.oldclickgui.vega;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.client.ClickGUI;
import com.kisman.cc.oldclickgui.vega.component.Frame;
import com.kisman.cc.oldclickgui.vega.component.components.Button;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class Gui extends GuiScreen {
    public ArrayList<Frame> frames;

    public Gui() {
        frames = new ArrayList<>();

        int x = 3;
        int y = 6;

        for(Category cat : Category.values()) {
            frames.add(new Frame(cat, x, y));

            x += 120;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scrollWheelCheck();
        for(Frame frame : frames) {
            frame.renderComponent();
            frame.updateComponent(mouseX, mouseY);
            for(Button b : frame.buttons) {
                b.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for(Frame frame : frames) {
            if(frame.open && keyCode != 1 && !frame.buttons.isEmpty()) {
                for(Button b : frame.buttons) {
                    b.keyTyped(typedChar, keyCode);
                }
            }
        }

        if(keyCode == 1) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Frame frame : frames) {
            if(frame.isMouseOnButton(mouseX, mouseY) && mouseButton == 0) {
                frame.dragging = true;
                frame.dragX = mouseX - frame.x;
                frame.dragY = mouseY - frame.y;
            }

            if(frame.isMouseOnButton(mouseX, mouseY) && mouseButton == 1) {
                frame.open = !frame.open;
            }

            if(frame.open) {
                if(!frame.buttons.isEmpty()) {
                    for(Button b : frame.buttons) {
                        b.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(Frame frame : frames) {
            frame.dragging = false;
        }

        for(Frame frame : frames) {
            if(frame.open && !frame.buttons.isEmpty()) {
                for(Button b : frame.buttons) {
                    b.mouseReleased(mouseX, mouseY, state);
                }
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    private void scrollWheelCheck() {
        int dWheel = Mouse.getDWheel();
        if(dWheel < 0){
            for(Frame frame : frames) {
                frame.y = frame.y - (int) ClickGUI.instance.scrollSpeed.getValDouble();
            }
        }
        else if(dWheel > 0){
            for(Frame frame : frames){
                frame.y = frame.y + (int) ClickGUI.instance.scrollSpeed.getValDouble();
            }
        }
    }
}
