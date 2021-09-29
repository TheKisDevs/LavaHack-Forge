package com.kisman.cc.newclickgui.component.catcomponents;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.newclickgui.component.Frame;
import com.kisman.cc.newclickgui.component.modulecomponents.ModuleButton;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;

public class CatButton {
//    private ArrayList<Module> modules;
    private ArrayList<ModuleButton> modules;

    private int moduleX;
    private int moduleY;
    private int moduleOffset;

    private Category cat;
    private ModuleButton listenSet;

    private int x;
    private int y;
    private int offset;
    private int width;

    private String name;

    private boolean listen;
    private boolean listenSetb;
    private boolean hover;

    private Frame parent;

    private Color b1 = new Color(0.3f, 0.3f, 0.3f, 0.8f);

    public CatButton(int x, int y, int offset, int width, String title, Frame parent, Category cat) {
        this.x = x;
        this.y = y;
        this.offset = offset;
        this.width = width;

        this.moduleX = parent.getBarX() + CustomFontUtil.getStringWidth(Category.MOVEMENT.name()) + 5;
        this.moduleY = parent.getBarY();
        this.moduleOffset = 0;

        this.name = title;

        this.parent = parent;
        this.cat = cat;

        this.modules = new ArrayList<>();

        Kisman.instance.moduleManager.modules.stream().filter(module -> module.getCategory() == this.cat).forEach(module -> {
            this.modules.add(new ModuleButton(this.moduleX, this.moduleY, this.moduleOffset, module.getName(), module, this));

            this.moduleOffset += CustomFontUtil.getFontHeight() + 2;
        });
    }

    public void renderComponent() {
        if(this.hover) GuiScreen.drawRect(this.x, this.y + this.offset, this.x + CustomFontUtil.getStringWidth(this.name) + 1, this.y + this.offset + 2 + CustomFontUtil.getFontHeight(), this.b1.getRGB());

        CustomFontUtil.drawStringWithShadow(this.name, this.x, this.y + this.offset, this.listen ? new Color(255, 0, 0, 255).getRGB() : -1);

        if(this.listen) {
            this.modules.stream().forEach(moduleButton -> {
                moduleButton.renderComponent();
            });
        }
    }

    public void updateComponent(int mouseX, int mouseY) {
        if(this.listen) {
            this.modules.stream().forEach(moduleButton -> {
                moduleButton.updateComponent(mouseX, mouseY);
            });
        }

        this.hover = isMouseOnButton(mouseX, mouseY);
    }

    public void keyTyped(char typedChar, int keyCode) {
        if(this.listen) {
            for (ModuleButton mod : modules) {
                mod.keyTyped(typedChar, keyCode);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {

            this.listen = !this.listen;

            if(this.listen) this.parent.setListenCat(this);

            return;
        }

        if(da(mouseX, mouseY) && button == 1) {
            if(this.listenSet != null) {
//                this.listenSet.setListen(false);
//                this.listenSet.setListenSet(null);
                for(ModuleButton mod : this.modules) {
                    mod.setListen(false);
                }
            }
        }

        if(this.parent.getCatListen() == this) {
            for(ModuleButton mod : this.modules) {
                mod.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if(this.listen) {
            for(ModuleButton mod : this.modules) {
                mod.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public boolean da(int x, int y) {
        if(x > this.moduleX && x < this.parent.getX() + 200 && y > this.moduleY && y < this.moduleY + this.offset) {
            return true;
        }

        return false;
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x > this.x && x < this.x + CustomFontUtil.getStringWidth(Category.MOVEMENT.name()) && y > this.y + this.offset && y < this.y + this.offset + CustomFontUtil.getFontHeight()) return true;

        return false;
    }

    private boolean isMouseOnModuleFrame(int x, int y) {
        if(x > this.moduleX - 1 && x < this.moduleX + 100 + 1 && y > this.moduleY - 1 && y < this.moduleY + this.offset + 1) return true;

        return false;
    }

    public boolean isListen() {
        return this.listen;
    }

    public void setListen(boolean listen) {
        this.listen = listen;
    }

    public String getName() {
        return this.name;
    }

    public ModuleButton getListenSet() {
        return listenSet;
    }

    public void setListenSet(ModuleButton listenSet) {
        this.listenSet = listenSet;
    }

    public boolean isListenSetB() {
        return listenSetb;
    }

    public void setListenSetB(boolean listenSet) {
        this.listenSetb = listenSet;
    }
}
