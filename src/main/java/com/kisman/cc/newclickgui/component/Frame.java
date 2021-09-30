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

    private CatButton listenCat;

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
        this.listenCat = null;

        this.catOffset = 0;
        this.catWidth = CustomFontUtil.getStringWidth(Category.MOVEMENT.name());

        for(Category cat : Category.values()) {
            this.cat.add(new CatButton(this.barX + 1, this.barY + 1, this.catOffset, this.catWidth, cat.name(), this, cat));

            this.catOffset += CustomFontUtil.getFontHeight() + 2;
        }
    }

    public void renderComponent() {
        GuiScreen.drawRect(x, y, x + width, y + heigth, new Color(0x252525).hashCode());
        GuiScreen.drawRect(x + 7, y + 7, x + width - 7, y + heigth - 7, new Color(0x151515).hashCode());

        renderCategories();
    }

    public void updateComponent(int mouseX, int mouseY) {
        this.cat.stream().forEach(catButton -> {
            catButton.updateComponent(mouseX, mouseY);
        });
    }

    public void keyTyped(char typedChar, int keyCode) {
        for(CatButton cat : this.cat) {
            cat.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnCategoryFrame(mouseX, mouseY)) {
            if (this.listenCat != null) {
                this.listenCat.setListen(false);
            }
        }

        mouseClickedComponent(mouseX, mouseY, button);
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        mouseReleasedComponent(mouseX, mouseY, button);
    }

    public boolean isMouseOnFrame(int x, int y) {
        if(x > this.x + 6 && x < this.x + width - 8 && y > this.y + 6 && this.y > this.y + this.heigth - 8) return true;

        return false;
    }

    public boolean isMouseOnCategoryFrame(int x, int y) {
        if(x > this.barX && x < this.barX + 2 + CustomFontUtil.getStringWidth(Category.MOVEMENT.name()) && y > this.barY && y < this.barY + this.catOffset) return true;

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

    private void mouseReleasedComponent(int mouseX, int mouseY, int button) {
        for(CatButton cat : this.cat) {
            cat.mouseReleased(mouseX, mouseY, button);
        }
    }

    private void setOff(int offset) {
        this.catOffset += offset;
    }

    public String getCatListenName() {
        return this.listenCat.getName();
    }

    public CatButton getCatListen() {
        return this.listenCat;
    }

    public void setListenCat(CatButton cat) {
        this.listenCat = cat;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getBarX() {
        return barX;
    }

    public void setBarX(int barX) {
        this.barX = barX;
    }

    public int getBarY() {
        return barY;
    }

    public void setBarY(int barY) {
        this.barY = barY;
    }

    public int getCatOffset() {
        return catOffset;
    }

    public void setCatOffset(int catOffset) {
        this.catOffset = catOffset;
    }

    public int getCatWidth() {
        return catWidth;
    }

    public void setCatWidth(int catWidth) {
        this.catWidth = catWidth;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
