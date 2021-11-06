package com.kisman.cc.oldclickgui.vega.component.components.sub;

import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.oldclickgui.vega.component.Component;

public class ModeButton extends Component {
    public Button b;
    public Module m;
    public int offset;

    public ModeButton(Button b, Module m, int offset) {
        this.b = b;
        this.m = m;
        this.offset = offset;
    }

    public void renderComponent() {

    }

    public void updateComponent(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {

    }

    public void mouseReleased(int mouseX, int mouseY, int button) {

    }

    public void newOff(int newOff) {

    }

    private boolean isMouseOnButton(int x, int y) {
        return true;
    }
}
