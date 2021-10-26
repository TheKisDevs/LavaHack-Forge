package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.*;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class ColorPickerSimpleButton extends Component{
    public float PICKER_HEIGHT = 64;

    private Setting set;
    public Button button;

    public int offset;

    public boolean open;

    public ColorPickerSimpleButton(Setting s, Button parent, int offset) {
        this.set = s;
        this.button = parent;
        this.offset = offset;
    }

    public void renderComponent() {
        Gui.drawRect(button.parent.getX() + 2, button.parent.getY() + offset, button.parent.getX() + 3, button.parent.getY() + offset + 12, new Color(ClickGui.getRLine(), ClickGui.getGLine(), ClickGui.getBLine(), ClickGui.getALine()).getRGB());

    }

    public void updateComponent(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        
    }
}
