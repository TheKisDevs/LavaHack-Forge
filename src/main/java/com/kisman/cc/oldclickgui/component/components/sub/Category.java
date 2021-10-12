package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.oldclickgui.component.components.sub.sub.LineButton;
import com.kisman.cc.oldclickgui.component.components.sub.sub.SubComponent;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Category extends Component {
    public boolean category;

    public int x;
    public int y;

    private ArrayList<SubComponent> components;

    private Button button;
    private Setting set;

    private int offset;
    private int opY;

    private boolean open;

    public Category(Button button, Setting set, int offset) {
        this.components = new ArrayList<>();

        this.category = true;

        this.button = button;
        this.set = set;
        this.offset = offset;

        this.x = button.parent.getX();
        this.y = button.parent.getY();

        Kisman.instance.settingsManager.getSubSettingsByMod(set.getParentMod(), set).forEach(setting -> {
            if(setting.isCategoryLine()) {
                this.components.add(new LineButton());//setting, offset, opY, this
                this.opY += 12;
            }
        });
    }

    public void setOff(int offset) {
        this.offset = offset;
    }

    public void renderComponent() {
        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, true ? -1 : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
        Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 5) + 5, (button.parent.getX() + 7 + button.parent.getWidth() - 7) - 3,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());
        GL11.glPushMatrix();
        GL11.glScalef(0.5f,0.5f, 0.5f);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(set.getTitle(), (button.parent.getX() + 4) * 2, (button.parent.getY() + offset) * 2 + 5, new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        GL11.glPopMatrix();

        if(this.open) {
            for(SubComponent comp : this.components) {
                comp.renderComponent();
            }
        }
    }

    public void updateComponent(int mouseX, int mouseY) {
        if(this.open) {
            this.components.forEach(component -> {
                component.updateComponent(mouseX, mouseY);
            });
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(this.open) {
            this.components.forEach(component -> {
                component.mouseClicked(mouseX, mouseY, button);
            });
        }

        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            this.open = !this.open;
            if(open) {
                this.button.setOff(opY + offset - 12, this);
            } else {
                this.button.setOff(offset - 12, this);
            }

            this.button.parent.refresh();
        }
    }

    public boolean isCategory() {
        return this.category;
    }

    public ArrayList<SubComponent> getComponents() { return this.components; }

    public boolean isMouseOnButton(int x, int y) {
        if(x > button.parent.getX() && x < button.parent.getX() + 88 && y > button.parent.getY() + offset && y < button.parent.getY() + offset + 12) return true;

        return false;
    }

    public int getHeight() {
        if(open) {
            return 12 * (components.size() + 1);
        } else {
            return 0;
        }
    }

}
