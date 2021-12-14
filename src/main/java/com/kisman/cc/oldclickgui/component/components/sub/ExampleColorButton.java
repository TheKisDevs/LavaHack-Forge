package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LineMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public class ExampleColorButton extends Component {
    private Setting set;
    private Button button;
    private int offset;

    private Color color;

    public ExampleColorButton(Setting set, Button button, int offset) {
        this.set = set;
        this.button = button;
        this.offset = offset;
    }

    public void renderComponent() {
        if(color != null) {
            Minecraft mc = Minecraft.getMinecraft();

            Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, color.getRGB());

            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(set.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
            GL11.glPopMatrix();

            Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

            if(ClickGui.getSetLineMode() == LineMode.SETTINGONLYSET || ClickGui.getSetLineMode() == LineMode.SETTINGALL) {
                Gui.drawRect(
                        button.parent.getX() + 88 - 3,
                        button.parent.getY() + offset,
                        button.parent.getX() + button.parent.getWidth() - 2,
                        button.parent.getY() + offset + 12,
                        new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB()
                );
            }
        }
    }

    public void updateComponent(int mouseX, int mouseY) {
        color = new Color(set.getRed() * 255, set.getGreen() * 255, set.getBlue() * 255, set.getAlpha() * 255);
    }

    public void newOff(int newOff) {
        this.offset = newOff;
    }
}
