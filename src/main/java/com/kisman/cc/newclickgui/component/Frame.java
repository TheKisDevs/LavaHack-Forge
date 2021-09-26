package com.kisman.cc.newclickgui.component;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.newclickgui.component.catcomponents.CatButton;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;

public class Frame {
    private ArrayList<CatButton> cat;

    private FontRenderer fr;

    private String title;

    private int x;
    private int y;
    private int barX;
    private int barY;
    private int catOffset;
    private int catWidth;
    private int heigth;
    private int width;

    private Color b1 = new Color(0.3f, 0.3f, 0.3f, 0.8f);
    private Color b2 = new Color(0.16f, 0.15f, 0.15f, 1);

    public Frame(int x, int y, int heigth, int width, FontRenderer fr) {
        this.title = Kisman.NAME + " | " + Kisman.VERSION;

        this.x = x;
        this.y = y;
        this.barX = x + 7;
        this.barY = y + 7;
        this.heigth = heigth;
        this.width = width;

        this.fr = fr;

        this.cat = new ArrayList<>();

        this.catOffset = 0;
        this.catWidth = CustomFontUtil.getStringWidth(Category.MOVEMENT.name());

        for(Category cat : Category.values()) {
            this.cat.add(new CatButton(this.barX + 1, this.barY + 1, this.catOffset, this.catWidth, cat.name()));

            this.catOffset += CustomFontUtil.getFontHeight() + 2;
        }
    }

    public void renderComponent() {
        GuiScreen.drawRect(x, y, x + width, y + heigth, b1.getRGB());
        GuiScreen.drawRect(x + 7, y + 7, x + width - 7, y + heigth - 7, b2.getRGB());

        renderCategories();
    }

    public void updateComponent(int mouseX, int mouseY) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {


/*        if(isMouseOnFrame(mouseX, mouseY)) {
            if(isMouseOnCategoryFrame(mouseX, mouseY) && button == 0) {
                 this.cat.stream().filter(catButton -> catButton.isListen()).forEach(catButton -> {
                     catButton.setListen(false);
                 });
            }
        }*/

        mouseClickedComponent(mouseX, mouseY, button);
    }

    public boolean isMouseOnFrame(int x, int y) {
        if(x > this.x + 6 && x < this.x + width - 8 && y > this.y + 6 && this.y > this.y + this.heigth - 8) return true;

        return false;
    }

    public boolean isMouseOnCategoryFrame(int x, int y) {
        if(x > this.barX && x < this.barX + 2 + fr.getStringWidth(Category.MOVEMENT.name()) && y > this.barY && y < this.barY + this.catOffset) return true;

        return false;
    }

    private void renderCategories() {
        for(CatButton cat : this.cat) {
            cat.renderComponent();
        }
    }

    private void mouseClickedComponent(int mouseX, int mouseY, int button) {
        for(CatButton cat : this.cat) {
            cat.mouseClicked(mouseX, mouseY, button);
        }
    }

    private void setOff(int offset) {
        this.catOffset += offset;
    }
}
