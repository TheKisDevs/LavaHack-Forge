package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.sub.sub.LineButton;
import com.kisman.cc.oldclickgui.component.components.sub.sub.SubComponent;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class CategoryButton extends Component {
    private ArrayList<SubComponent> subSubComponent;

    private ColorUtil colorUtil = new ColorUtil();
    private Minecraft mc = Minecraft.getMinecraft();
    private ScaledResolution sr = new ScaledResolution(mc);
    public Button button;
    public Setting line;

    public int x = 0;
    public int y = 0;
    public int x1;
    public int y1;
    public int x2;
    public int y2;
    public int offset;
    public int index = 0;

    public boolean open = false;

    public CategoryButton(Setting line, Button button, int offset, int index) {
        this.subSubComponent = new ArrayList<>();
        this.button = button;
        this.line = line;
        this.x1 = button.parent.getX();
        this.y1 = button.parent.getY();
        this.x2 = button.parent.getX() + button.parent.getWidth();
        this.y2 = button.parent.getY() + button.offset;
        this.offset = offset;
        this.index = index;

        for(Setting set : Kisman.instance.settingsManager.getSettings()) {
            if (Kisman.instance.settingsManager.getSettingByIndex(this.index).getIndex() == this.index) {
                set.getSetParent();
                LineButton lineButton = new LineButton();
                if(set.isCategoryLine()) {
                    this.subSubComponent.add(lineButton);
                }
            }
        }
    }

    @Override
    public void renderComponent() {
        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1), button.parent.getY() + offset + 12, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
        Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, button.parent.getX() + 7 + button.parent.getWidth() - 7,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, ClickGui.isRainbowLine() ? colorUtil.getColor() : new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
        GL11.glPushMatrix();
        GL11.glScalef(0.5f,0.5f, 0.5f);
        CustomFontUtil.drawString(line.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        GL11.glPopMatrix();
        if(open) {
            //Minecraft.getMinecraft().player.sendChatMessage("i open category!!!");
            //Gui.drawRect( mc.displayWidth / 2, mc.displayHeight / 2, mc.displayWidth / 2 + 88, mc.displayHeight / 2 + 12, -1);
            Gui.drawRect((sr.getScaledWidth() / 2) * 2 - 44, (sr.getScaledHeight() / 2) * 2 - 6, (sr.getScaledWidth() / 2) * 2 + 44, (sr.getScaledHeight() / 2) * 2 + 7, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
            //Gui.drawRect(((mc.displayWidth / 2)), ((mc.displayHeight / 2)), ((mc.displayWidth / 2) + 44), ((mc.displayHeight / 2) + 6), new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);

            //Gui.drawRect(sr.getScaledWidth() / 2 - 44, sr.getScaledHeight() / 2 - 6, sr.getScaledWidth() / 2 + 88, sr.getScaledHeight() / 2 + 12, new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
            mc.fontRenderer.drawString(line.getTitle(), ((mc.displayWidth / 2) - 42), ((mc.displayHeight / 2) - 5), -1);
            GL11.glPopMatrix();
            //(this.x + 2) * 2 + 5, (this.y + 2.5f)
            //((12 - mc.fontRenderer.FONT_HEIGHT) / 2)
            for(SubComponent subComp : subSubComponent) {
                subComp.renderComponent();
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            Minecraft.getMinecraft().player.sendChatMessage("i open category!!!1");
            this.x = mouseX;
            this.y = mouseY;
            this.open = !this.open;
        }
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            this.open = false;
            Minecraft.getMinecraft().player.sendChatMessage("i open category!!!2");
        }
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x > button.parent.getX() && x < button.parent.getX() + 88 && y > button.parent.getY() + offset && y < button.parent.getY() + 12 + offset) {
            return true;
        }
        return false;
    }
}
