package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.oldclickgui.component.components.sub.itemsButton.IButton;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LineMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class ItemsButton extends Component {
    private Minecraft mc = Minecraft.getMinecraft();

    private ArrayList<IButton> buttons;

    public Button button;
    public Setting set;

    public int offset;
    private boolean hover;

    private int armourCompress = 2;
    private int armourSpacing = 20;

    public int blocksOffset;
    public boolean open;

    public ItemsButton(Button button, Setting set, int offset) {
        this.button = button;
        this.set = set;
        this.offset = offset;

        this.buttons = new ArrayList<>();

        //offsets

        int iterations = 0;
        for(int i = 0; i < set.getItems().length; i++) {
            this.buttons.add(new IButton(button.x + 3 + (iterations * (armourCompress + armourSpacing)), button.y + this.offset + 12, set.getItems()[i], offset + 12));

            iterations++;
        }
    }

    public void renderComponent() {
        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + (button.parent.getWidth() * 1) - 3, button.parent.getY() + offset + 12, hover ? new Color(ClickGui.getRHoveredModule(), ClickGui.getGHoveredModule(), ClickGui.getBHoveredModule(), ClickGui.getAHoveredModule()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());
        Gui.drawRect(button.parent.getX() + 3, (button.parent.getY() + offset + mc.fontRenderer.FONT_HEIGHT - 5) + 5, (button.parent.getX() + 7 + button.parent.getWidth() - 7) - 3,(button.parent.getY() + offset + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 4) + 5, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

        //render items
        if(open) {
            buttons.stream().forEach(iButton -> {
                iButton.render();
            });
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.5f,0.5f, 0.5f);
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

    public void updateComponent(int mouseX, int mouseY) {
        hover = isMouseOnButton(mouseX, mouseY);

        buttons.stream().forEach(iButton -> {
            iButton.update(mouseX, mouseY);
        });
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            open = !open;
        }

        buttons.stream().forEach(iButton -> {
            iButton.mouseClicked(mouseX, mouseY, button);
        });
    }

    public void setOff(int newOff) {
        offset = newOff;

        buttons.stream().forEach(iButton -> {
            iButton.setOff(newOff + 12);
        });
    }

    private boolean isMouseOnButton(int x, int y) {
        if(x > button.parent.getX() && x < button.parent.getX() + 88 && y > button.parent.getY() + offset && y < button.parent.getY() + offset + 12) return true;

        return false;
    }
}
