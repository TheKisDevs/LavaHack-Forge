package com.kisman.cc.oldclickgui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

import com.kisman.cc.util.customfont.CustomFontUtil;

public class ColorPicker extends GuiScreen {

    private final float[] color;
    private boolean pickingColor;
    private boolean pickingHue;
    private boolean pickingAlpha;
    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight;
    private int alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight;
    private float rainbowSpeed = 20.0f;
    private boolean rainbowState = false;

    private int selectedColorFinal;

    public ColorPicker() {
        this.color = new float[] {0.4f, 1.0f, 1.0f, 1.0f};
        this.pickingColor = false;
    }

    @Override
    public void initGui() {
        this.pickerWidth = 120;
        this.pickerHeight = 100;
        this.pickerX = this.width / 2 - pickerWidth / 2;
        this.pickerY = this.height / 2 - pickerHeight / 2;
        this.hueSliderX = pickerX;
        this.hueSliderY = pickerY + pickerHeight + 6;
        this.hueSliderWidth = pickerWidth;
        this.hueSliderHeight = 10;
        this.alphaSliderX = pickerX + pickerWidth + 6;
        this.alphaSliderY = pickerY;
        this.alphaSliderWidth = 10;
        this.alphaSliderHeight = pickerHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // if (this.rainbowState) {
        //     double rainbowState = Math.ceil((System.currentTimeMillis() + 200) / 20.0);
        //     rainbowState %= 360.0;
        //     this.color[0] = (float) (rainbowState / 360.0);
        // }
        this.drawDefaultBackground();
        if (this.pickingHue) {
            if (this.hueSliderWidth > this.hueSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
                this.color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(hueSliderY, mouseY), hueSliderY + hueSliderHeight);
                this.color[0] = (restrictedY - (float) hueSliderY) / hueSliderHeight;
            }
        }
        if (this.pickingAlpha) {
            if (this.alphaSliderWidth > this.alphaSliderHeight) {
                float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + alphaSliderWidth);
                this.color[3] = 1 - (restrictedX - (float) alphaSliderX) / alphaSliderWidth;
            } else {
                float restrictedY = (float) Math.min(Math.max(alphaSliderY, mouseY), alphaSliderY + alphaSliderHeight);
                this.color[3] = 1 - (restrictedY - (float) alphaSliderY) / alphaSliderHeight;
            }
        }
        if (this.pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            this.color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            this.color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
        }
        int selectedX = pickerX + pickerWidth + 6;
        int selectedY = pickerY + pickerHeight + 6;
        int selectedWidth = 10;
        int selectedHeight = 10;
        Gui.drawRect(pickerX - 2, pickerY - 2, pickerX + pickerWidth + 2, pickerY + pickerHeight + 2, 0xFC000000);
        Gui.drawRect(hueSliderX - 2, hueSliderY - 2, hueSliderX + hueSliderWidth + 2, hueSliderY + hueSliderHeight + 2, 0xFC000000);
        Gui.drawRect(alphaSliderX - 2, alphaSliderY - 2, alphaSliderX + alphaSliderWidth + 2, alphaSliderY + alphaSliderHeight + 2, 0xFC000000);
        int selectedColor = Color.HSBtoRGB(this.color[0], 1.0f, 1.0f);
        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;
        this.drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, this.color[3]);
        this.drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, this.color[0]);
        this.drawAlphaSlider(alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, this.color[3]);
        //final int 
        this.selectedColorFinal = alpha(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])), this.color[3]);
        Gui.drawRect(selectedX - 2, selectedY - 2, selectedX + selectedWidth + 2, selectedY + selectedHeight + 2, 0xFC000000);
        Gui.drawRect(selectedX, selectedY, selectedX + selectedWidth, selectedY + selectedHeight, this.selectedColorFinal);

        Gui.drawRect(selectedX - 2, selectedY + (selectedHeight * 2) - 2, selectedX + 2 + selectedWidth, selectedY + (selectedHeight * 3) + 2, 0xFC000000);
        CustomFontUtil.drawString("RainBow", selectedX - 2 - CustomFontUtil.getStringWidth("RainBow"), (selectedY + (selectedHeight * 2) - ((selectedHeight - CustomFontUtil.getFontHeight()) / 2)), 0xFC000000);
        if(rainbowState) {
            Gui.drawRect(selectedX, selectedY + (selectedHeight * 2), selectedX + selectedWidth, selectedY + (selectedHeight * 3), -1);
        }

        {
            final int cursorX = (int) (pickerX + color[1]*pickerWidth);
            final int cursorY = (int) ((pickerY + pickerHeight) - color[2]*pickerHeight);
            Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
        }
//        for (int i = 1; i < pickerHeight/10; i++) {
//            Gui.drawRect(selectedX - 2, pickerY + i * 14, selectedX + 12, pickerY + i * 14, 0xFC000000);
//        }
    }

    final int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha).getRGB();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.pickingColor = check(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY);
        this.pickingHue = check(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY);
        this.pickingAlpha = check(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.pickingColor = this.pickingHue = this.pickingAlpha = false;
    }

    private void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            Gui.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                this.drawGradientRect(x, y + step * (height/6), x + width, y + (step+1) * (height/6), previousStep, nextStep);
                step++;
            }
            final int sliderMinY = (int) (y + (height*hue)) - 4;
            Gui.drawRect(x, sliderMinY - 1, x+width, sliderMinY + 1, -1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                this.gradient(x + step * (width/6), y, x + (step+1) * (width/6), y + height, previousStep, nextStep, true);
                step++;
            }
            final int sliderMinX = (int) (x + (width*hue));
            Gui.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        }
    }

    private void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        boolean left = true;
        int checkerBoardSquareSize = width/2;
        for (int squareIndex = -checkerBoardSquareSize; squareIndex < height; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                Gui.drawRect(x, y + squareIndex, x + width, y + squareIndex + checkerBoardSquareSize, 0xFFFFFFFF);
                Gui.drawRect(x + checkerBoardSquareSize, y + squareIndex, x + width, y + squareIndex + checkerBoardSquareSize, 0xFF909090);
                if (squareIndex < height - checkerBoardSquareSize) {
                    int minY = y + squareIndex + checkerBoardSquareSize;
                    int maxY = Math.min(y + height, y + squareIndex + checkerBoardSquareSize*2);
                    Gui.drawRect(x, minY, x + width, maxY, 0xFF909090);
                    Gui.drawRect(x + checkerBoardSquareSize, minY, x + width, maxY, 0xFFFFFFFF);
                }
            }
            left = !left;
        }
        this.gradient(x, y, x + width, y + height, new Color(red, green, blue, alpha).getRGB(), 0, false);
        final int sliderMinY = (int) (y + height - (height * alpha));
        Gui.drawRect(x, sliderMinY - 1, x+width, sliderMinY + 1, -1);
    }

    private void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glVertex2f(pickerX, pickerY);
            GL11.glVertex2f(pickerX, pickerY + pickerHeight);
            GL11.glColor4f(red, green, blue, alpha);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2f(pickerX, pickerY);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex2f(pickerX, pickerY + pickerHeight);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2f(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {

            final float startA = (startColor >> 24 & 0xFF) / 255.0f;
            final float startR = (startColor >> 16 & 0xFF) / 255.0f;
            final float startG= (startColor >> 8 & 0xFF) / 255.0f;
            final float startB = (startColor & 0xFF) / 255.0f;

            final float endA = (endColor >> 24 & 0xFF) / 255.0f;
            final float endR = (endColor >> 16 & 0xFF) / 255.0f;
            final float endG = (endColor >> 8 & 0xFF) / 255.0f;
            final float endB = (endColor & 0xFF) / 255.0f;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }

    protected boolean check(int minX, int minY, int maxX, int maxY, int curX, int curY) {
        return curX >= minX && curY >= minY && curX < maxX && curY < maxY;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_R) {
            this.rainbowState = !this.rainbowState;
        }
        if (keyCode == Keyboard.KEY_LEFT) {
            this.rainbowSpeed -= 0.1;
        } else if (keyCode == Keyboard.KEY_RIGHT) this.rainbowSpeed += 0.1;
    }

    public int getColor() {
        return this.selectedColorFinal;
    }

    public void setColor(int color) {
        this.selectedColorFinal = color;
    }

    public float getColor(int index) {
        try {
            return this.color[index];
        } catch(Exception e) {
            return this.color[2];
            //e.printStackTrace();
        }
    }

    public void setColor(int index, float color) {
        try {
            this.color[index] = color;
        } catch(Exception e) {
            this.color[3] = color;
        }
    }

    public boolean isRainbowState() {
        return this.rainbowState;
    }

    public void setRainbowState(boolean rainbowState) {
        this.rainbowState = rainbowState;
    }
}