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
    private Button button;
    private Setting line;
    private ColorUtil colorUtil = new ColorUtil();

    private int x;
    private int y;
    private int x1;
    private int y1;
    private int offset;

    public Line(Setting line, Button button, int offset) {
        this.button = button;
        this.line = line;
        this.x = button.parent.getX();
        this.y = button.parent.getY();
        this.x1 = button.parent.getX() + button.parent.getWidth();
        this.y1 = button.parent.getY() + button.offset;
        this.offset = offset;
    }

    public void setOff(int offset) {
        this.offset = offset;
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1), button.parent.getY() + offset + 12, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
        Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, button.parent.getX() + 7 + button.parent.getWidth() - 7,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
        GL11.glPushMatrix();
        GL11.glScalef(0.5f,0.5f, 0.5f);
        CustomFontUtil.drawString(line.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        GL11.glPopMatrix();
    }
}
