package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.StringEvent;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LineMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class StringButton extends Component {
    private Minecraft mc = Minecraft.getMinecraft();
    private FontRenderer fr = mc.fontRenderer;

    public Setting set;
    public Button button;
    public int offset;
    public int x, y;

    private String currentString = "";
    private String dString;
    private String regex = "-*[1-9][0-9]*";

    private boolean active = false;

    public StringButton(Setting set, Button button, int offset) {
        this.set = set;
        this.button = button;
        this.offset = offset;
        this.x = button.parent.getX();
        this.y = button.parent.getY();
        this.dString = set.getdString();
    }

    public void setOff(int offset) {
        this.offset = offset;
    }

    public void renderComponent() {
        GuiScreen.drawRect(this.button.parent.getX(), this.button.parent.getY() + offset, this.button.parent.getX() + 88, this.button.parent.getY() + 12 + offset, this.active ? new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());

        if(this.active) {
            fr.drawStringWithShadow(this.currentString + "_", button.parent.getX() + 4, button.parent.getY() + offset + 1 + ((12 - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        } else if(!this.active){
            fr.drawStringWithShadow(this.currentString.isEmpty() ? this.set.getdString() : this.currentString, button.parent.getX() + 4, button.parent.getY() + offset + 1 + ((12 - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        }

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

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0 && set.isOpening()) {
            this.active = !this.active;

            if(!active) {
                enterString();
            }
        }
    }

    public void keyTyped(char typedChar, int key) {
        StringEvent event1 = new StringEvent(set, "" + typedChar, Event.Era.PRE, active);
        Kisman.EVENT_BUS.post(event1);

        if(event1.isCancelled()) {
            return;
        }

        if(key == 1) return;

        if(Keyboard.KEY_RETURN == key && this.active) {
            this.enterString();
        } else if(key == 14 && this.active) {
            if(currentString != null){
                if (!this.currentString.isEmpty()) {
                    this.currentString = this.currentString.substring(0, this.currentString.length() - 1);
                }
            } else {
                active = false;
            }
        } else if(ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.active && !set.isOnlyNumbers()) {
            this.setString(this.currentString + typedChar);

            StringEvent event2 = new StringEvent(set, "" + typedChar, Event.Era.POST, active);
            Kisman.EVENT_BUS.post(event2);

            if(event2.isCancelled()) {
                active = false;
                return;
            }

            if(set.isOnlyOneWord() && this.active) {
                this.active = false;
            }
        }
    }

    private boolean isMouseOnButton(int x, int y) {
        if(x > this.button.parent.getX() && x < this.button.parent.getX() + 88 && y > this.button.parent.getY() + offset && y < this.button.parent.getY() + 12 + offset) return true;

        return false;
    }

    private void setString(String newString) {
        this.currentString = newString;
    }

    private String removeLastChar(String str) {
        String output = "";
        if (str != null && str.length() > 0) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    private void enterString() {
        this.active = false;

        if (this.currentString.isEmpty()) {
            this.set.setValString(this.set.getdString());
        } else {
            this.set.setValString(this.currentString);
        }
    }
}
