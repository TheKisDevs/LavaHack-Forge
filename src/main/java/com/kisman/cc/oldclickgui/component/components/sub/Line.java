package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Line extends Component {
    private boolean cat;

    public Button button;
    private Category parent;
    private Setting line;
    private ColorUtil colorUtil = new ColorUtil();

    public int offset;
    private int cOffset;
    public int x, y;

    public Line(Setting line, Button button, int offset) {
        this.button = button;
        this.line = line;
        this.cat = false;
        this.offset = offset;
        this.x = button.parent.getX();
        this.y = button.parent.getY();
    }

    public Line(Setting line, int offset, int cOffset, Category parent) {
        this.line = line;
        this.cat = true;
        this.parent = parent;
        this.offset = offset;
        this.cOffset = cOffset;
    }

    public void setSubOff(int offset) {

    }

    public void setOff(int offset) {
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        if(!cat) {
            Gui.drawRect(button.parent.getX() + (this.cat ? 4 : 3), button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - (this.cat ? 4 : 3), button.parent.getY() + offset + 12, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
            Gui.drawRect(button.parent.getX() + (this.cat ? 4 : 3), (button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, (button.parent.getX() + 7 + button.parent.getWidth() - 7) - (this.cat ? 4 : 3),(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
            GL11.glPushMatrix();
            GL11.glScalef(0.5f,0.5f, 0.5f);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(line.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
            GL11.glPopMatrix();
        } else {
            System.out.println("test");
            Gui.drawRect(parent.x + (this.cat ? 4 : 3), parent.y + offset + cOffset, parent.x + (88 * 1) - (this.cat ? 4 : 3), parent.y + offset + cOffset + 12, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
            Gui.drawRect(parent.x + (this.cat ? 4 : 3), (parent.y + offset + cOffset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, (parent.x + 7 + 88 - 7) - (this.cat ? 4 : 3),(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
            GL11.glPushMatrix();
            GL11.glScalef(0.5f,0.5f, 0.5f);
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(line.getTitle(), (parent.x + 4) * 2, (parent.y + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
            GL11.glPopMatrix();
        }

        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

    }
}
