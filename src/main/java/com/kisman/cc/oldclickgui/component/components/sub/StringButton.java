package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class StringButton extends Component {
    private Minecraft mc = Minecraft.getMinecraft();
    private FontRenderer fr = mc.fontRenderer;

    private Setting set;
    private Button button;
    private int offset;

    private String currentString = "";
    private String dString;

    private boolean active = false;

    public StringButton(Setting set, Button button, int offset) {
        this.set = set;
        this.button = button;
        this.offset = offset;
        this.dString = set.getdString();
    }

    public void renderComponent() {
        GuiScreen.drawRect(this.button.parent.getX(), this.button.parent.getY() + offset, this.button.parent.getX() + 88, this.button.parent.getY() + 12 + offset, this.active ? new Color(ClickGui.getRBackground(), ClickGui.getGBackground(), ClickGui.getBBackground(), ClickGui.getABackground()).getRGB() : new Color(ClickGui.getRNoHoveredModule(), ClickGui.getGNoHoveredModule(), ClickGui.getBNoHoveredModule(), ClickGui.getANoHoveredModule()).getRGB());

        if(this.active) {
            fr.drawStringWithShadow(this.currentString + "_", button.parent.getX() + 4, button.parent.getY() + offset + 1 + ((12 - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        } else if(!this.active){
            fr.drawStringWithShadow(this.currentString.isEmpty() ? this.set.getdString() : this.currentString, button.parent.getX() + 4, button.parent.getY() + offset + 1 + ((12 - fr.FONT_HEIGHT) / 2), new Color(ClickGui.getRText(), ClickGui.getGText(), ClickGui.getBText(), ClickGui.getAText()).getRGB());
        }
    }

    public void updateComponent(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0 && set.isOpening()) {
            /*if(this.currentString.equalsIgnoreCase("") && this.active) this.currentString = this.dString;*/

            this.active = !this.active;
        }
    }

    public void keyTyped(char typedChar, int key) {
        if(key == 1) return;

        if(Keyboard.KEY_RETURN == key && this.active) {
            this.enterString();

            /*if(!this.currentString.equalsIgnoreCase("")) this.set.setValString(this.currentString);*/
        } else if(key == 14 && this.active) {
            if(!this.currentString.isEmpty() && this.currentString != null) {
                System.out.println("lox");
                this.currentString = this.currentString.substring(0, this.currentString.length() - 1);
            }
/*        } else if(key == 47 && (Keyboard.isKeyDown(157) || Keyboard.isKeyDown(29))) {
            try {
                this.setString(this.removeLastChar(this.currentString));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        } else if(ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.active) {
            this.setString(this.currentString + typedChar);
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
