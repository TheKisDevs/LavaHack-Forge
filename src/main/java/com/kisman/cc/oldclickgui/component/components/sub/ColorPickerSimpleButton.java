package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.*;

public class ColorPickerSimpleButton extends Component{
    private Setting set;
    private Button parent;

    private int offset;

    public ColorPickerSimpleButton(Setting s, Button parent, int offset) {
        this.set = s;
        this.parent = parent;
        this.offset = offset;
    }

    public void renderComponent() {

    }

    public void updateComponent(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        
    }
}
