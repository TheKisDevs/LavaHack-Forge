package com.kisman.cc.oldclickgui.halq.component.components;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.halq.HalqGui;
import com.kisman.cc.oldclickgui.halq.component.Component;
import com.kisman.cc.oldclickgui.halq.component.components.sub.BindButton;
import com.kisman.cc.oldclickgui.halq.component.components.sub.CheckBox;
import com.kisman.cc.oldclickgui.halq.component.components.sub.ModeButton;
import com.kisman.cc.oldclickgui.halq.component.components.sub.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.render.objects.AbstractGradient;
import com.kisman.cc.util.render.objects.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.util.ArrayList;

public class Button extends Component {
    public final ArrayList<Component> comps = new ArrayList<>();
    public final Module mod;
    public int x, y, offset;
    public boolean open = false;

    public Button(Module mod, int x, int y, int offset) {
        this.mod = mod;
        this.x = x;
        this.y = y;
        this.offset = offset;

        int offsetY = offset + HalqGui.height;

        comps.add(new BindButton(mod, x, y, offsetY));
        offsetY += HalqGui.height;

        if(Kisman.instance.settingsManager.getSettingsByMod(mod) == null) return;
        for(Setting set : Kisman.instance.settingsManager.getSettingsByMod(mod)) {
            if(set.isSlider()) {
                comps.add(new Slider(set, x, y, offsetY));
                offsetY += HalqGui.height;
            }
            if(set.isCheck()) {
                comps.add(new CheckBox(set, x, y, offsetY));
                offsetY += HalqGui.height;
            }
            if(set.isBind()) {
                comps.add(new BindButton(set, x, y, offsetY));
                offsetY += HalqGui.height;
            }
            if(set.isCombo()) {
                comps.add(new ModeButton(set, x, y, offsetY));
                offsetY += HalqGui.height;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(HalqGui.shadowCheckBox) {
            Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, HalqGui.backgroundColor.getRGB());
            if(mod.isToggled()) Render2DUtil.drawAbstract(new AbstractGradient(new Vec4d(new double[] {x + HalqGui.width / 2, y + offset}, new double[] {x + HalqGui.width, y + offset}, new double[] {x + HalqGui.width, y + offset + HalqGui.height}, new double[] {x + HalqGui.width / 2, y + offset + HalqGui.height}), ColorUtils.injectAlpha(HalqGui.backgroundColor, 1), HalqGui.primaryColor));
        } else Render2DUtil.drawRectWH(x, y + offset, HalqGui.width, HalqGui.height, mod.isToggled() ? HalqGui.primaryColor.getRGB() : HalqGui.backgroundColor.getRGB());

        HalqGui.drawString(mod.getName(), x, y + offset, HalqGui.width, HalqGui.height);

         if(open && !comps.isEmpty()) {
             int height = 0;
             for(Component comp : comps) {
                 comp.drawScreen(mouseX, mouseY);
                 height += comp.getHeight();
             }
             if(HalqGui.test) {
                 Render2DUtil.drawRectWH(x, y + offset + HalqGui.height, HalqGui.width, 1, HalqGui.primaryColor.getRGB());
                 Render2DUtil.drawRectWH(x, y + offset + HalqGui.height + height - 1, HalqGui.width, 1, HalqGui.primaryColor.getRGB());
             }
         }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) mod.toggle();
        if(isMouseOnButton(mouseX, mouseY) && button == 1) open = !open;
        if(open && !comps.isEmpty()) for(Component comp : comps) comp.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(open && !comps.isEmpty()) for(Component comp : comps) comp.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateComponent(int x, int y) {
        this.x = x;
        this.y = y;
        if(open && !comps.isEmpty()) for(Component comp : comps) comp.updateComponent(x, y);
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if(open && !comps.isEmpty()) for(Component comp : comps) comp.keyTyped(typedChar, key);
    }

    @Override
    public void setOff(int newOff) {
        this.offset = newOff;
    }

    @Override
    public int getHeight() {
        return HalqGui.height + comps.size() * HalqGui.height;
    }

    private boolean isMouseOnButton(int x, int y) {
        return x > this.x && x < this.x + HalqGui.width && y > this.y + offset && y < this.y + HalqGui.height + offset;
    }
}
