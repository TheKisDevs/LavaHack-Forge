package com.kisman.cc.oldclickgui.component.components.sub;

import com.kisman.cc.oldclickgui.component.Component;
import com.kisman.cc.oldclickgui.component.components.Button;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderHelper;

import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import i.gishreloaded.gishcode.wrappers.*;

import java.util.function.*;
import java.awt.*;

public class ColorPicker extends Component{
    // private final int width = 85;
    // private final int height = 85;
    private int offset;

    private final int radius;

    private final Consumer<Integer> color;

    private int selectedX, selectedY;

    private Setting colorPicker;
    private Button button;

    private ResourceLocation COLOR_PICKER_TEXTURE = new ResourceLocation("kismancc", "setting/colorPicker.png");

    public ColorPicker(Setting colorPicker, Button button, int offset, int radius, Color selectedColor, Consumer<Integer> color) {//
        this.offset = offset;
        this.colorPicker = colorPicker;
        this.button = button;
        this.radius = radius;
        this.setColor(selectedColor);
        this.color = color;
    }

    @Override
    public void renderComponent() {
        RenderHelper.drawColoredCircle((int) (85 / 2d), (int) (85 / 2d), radius);
        RenderHelper.drawCircle(
            (int) (85 / 2d) + this.selectedX, 
            (int) (85 / 2d) + this.selectedY, 
            (int) (85 / 2), 
            0xFF121212
        );
        RenderHelper.drawCircle(
            (int) (85 / 2d) + this.selectedX, 
            (int) (85 / 2d) + this.selectedY, 
            (int) (85 / 2), 
            -1
        );
    }

    @Override
    public void updateComponent(int mouseX, int mouseY) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isPointInCircle((int) (85 / 2d), (int) (85 / 2d), this.radius, mouseX, mouseY) && button == 0) {
            this.selectedX = mouseX - (int) (85 / 2d);
            this.selectedY = mouseY - (int) (85 / 2d);
        }
    }

    private float getNormalized() {
        return (float) ((-Math.toDegrees(Math.atan2(this.selectedY, this.selectedX)) + 450) % 360) / 360;
    }

    private int getColor() {
        return  Color.getHSBColor(
            this.getNormalized(), 
            (float) (Math.hypot(this.selectedX, this.selectedY / this.radius)), 
            1)
        .getRGB();
    }

    private void setColor(Color selectedColor) {
        float[] hsb = selectedColor.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
    
        this.selectedX = 
            (int) hsb[1] * this.radius * (int) (Math.sin(Math.toRadians(hsb[8] * 360))) / (int) Math.sin(Math.toRadians(90));
        this.selectedY = 
            (int) hsb[1] * this.radius * (int) (Math.sin(Math.toRadians(90 - (hsb[8] * 360)))) / (int) Math.sin(Math.toRadians(90));
    }

    public boolean isPointInCircle(int x, int y, int radius, int pX, int pY) {
        return ((pX - x) * (pX - x)) + ((pY - y) * (pY - y)) <= radius * radius;
    }
}
