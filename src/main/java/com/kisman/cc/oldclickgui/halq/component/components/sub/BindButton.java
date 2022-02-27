package com.kisman.cc.oldclickgui.halq.component.components.sub;

import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.halq.HalqGui;
import com.kisman.cc.oldclickgui.halq.component.Component;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import org.lwjgl.input.Keyboard;

public class BindButton extends Component {
    private final Setting setting;
    private final Module module;
    private int x, y, offset;
    private boolean changing;

    public BindButton(Setting setting, int x, int y, int offset) {
        this.setting = setting;
        this.module = null;
        this.x = x;
        this.y = y;
        this.offset = offset;
    }

    public BindButton(Module module, int x, int y, int offset) {
        this.setting = null;
        this.module = module;
        this.x = x;
        this.y = y;
        this.offset = offset;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(setting == null && module == null) return;

        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(changing) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.primaryColor));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, changing ? HalqGui.primaryColor.getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(changing ? "Press a key..." : module != null ? "Bind: " + Keyboard.getKeyName(module.getKey()) : setting.getName() + ": " + Keyboard.getKeyName(setting.getKey()) , x, y + offset, HalqGui.width, HalqGui.height);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) changing = !changing;
        if(isMouseOnButton(mouseX, mouseY) && button == 1) {
            changing = false;
            if(module != null) module.setKey(Keyboard.KEY_NONE);
            else setting.setKey(Keyboard.KEY_NONE);
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(changing) {
            if(module == null && setting == null) return;
            if(module != null) module.setKey(key);
            else setting.setKey(key);
            changing = false;
        }
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height;
    }

    public boolean visible() {return setting == null || setting.isVisible();}

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + offset + HalqGui.height;
    }
}
