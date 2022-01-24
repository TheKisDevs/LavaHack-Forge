package com.kisman.cc.oldclickgui.csgo.components;

import com.kisman.cc.oldclickgui.csgo.*;
import com.kisman.cc.oldclickgui.csgo.Window;
import com.kisman.cc.util.*;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.kisman.cc.util.Render2DUtil.drawGradientRect;

public class ColorButton extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 24;

    private final int preferredWidth;
    private final int preferredHeight;
    private boolean hovered;
    private boolean opened;

    private Colour value;

    private float[] color;
    private boolean pickingColor;
    private boolean pickingHue;
    private boolean pickingAlpha;
    private int pickerX, pickerY, pickerWidth, pickerHeight;
    private int hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight;
    private int alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight;
    private int selectedColorFinal;

    private ValueChangeListener<Colour> listener;

    public ColorButton(IRenderer renderer, Colour colour, int preferredWidth, int preferredHeight) {
        super(renderer);
        this.preferredWidth = preferredWidth;
        this.preferredHeight = preferredHeight;
        this.value = colour;
        this.color = new float[] {colour.r1, colour.g1, colour.b1, 1};
        this.pickingColor = false;

        this.pickerWidth = 120;
        this.pickerHeight = 100;
        this.pickerX = x / 2 + pickerWidth;
        this.pickerY = y / 2 + pickerHeight + preferredHeight;
        this.hueSliderX = pickerX;
        this.hueSliderY = pickerY + pickerHeight + 6;
        this.hueSliderWidth = pickerWidth;
        this.hueSliderHeight = 10;
        this.alphaSliderX = pickerX + pickerWidth + 6;
        this.alphaSliderY = pickerY;
        this.alphaSliderWidth = 10;
        this.alphaSliderHeight = pickerHeight;

        updateWidth();
        updateHeight();
    }

    public ColorButton(IRenderer renderer, Colour colour) {
        this(renderer, colour, PREFERRED_WIDTH, PREFERRED_HEIGHT);
    }

    private void updateWidth() {
        if(opened) setWidth((pickerWidth + 6 + alphaSliderWidth) * 2);
        else setWidth(preferredWidth);
    }

    private void updateHeight() {
        if (opened) setHeight(preferredHeight + (pickerHeight + 6 + hueSliderHeight) * 2);
        else setHeight(preferredHeight);
    }

    @Override
    public void render() {
        this.pickerX = x / 2;
        this.pickerY = y / 2 + preferredHeight;
        this.hueSliderX = pickerX;
        this.hueSliderY = pickerY + pickerHeight + 6;
        this.hueSliderWidth = pickerWidth;
        this.hueSliderHeight = 10;
        this.alphaSliderX = pickerX + pickerWidth + 6;
        this.alphaSliderY = pickerY;
        this.alphaSliderWidth = 10;
        this.alphaSliderHeight = pickerHeight;
        this.color = new float[] {value.r1, value.g1, value.b1, value.a1};
        updateWidth();
        updateHeight();

        try {if(!opened) renderer.drawRect(x, y, getWidth(), getHeight(), value.getColor());} catch(Exception ignored) {}
        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, (hovered) ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        if (opened) {
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
            int selectedColor = Color.HSBtoRGB(this.color[0], 1.0f, 1.0f);
            float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
            float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
            float selectedBlue = (selectedColor & 0xFF) / 255.0f;
            this.drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue, this.color[3]);
            this.drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth, hueSliderHeight, this.color[0]);
            this.drawAlphaSlider(alphaSliderX, alphaSliderY, alphaSliderWidth, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, this.color[3]);
            //final int
            this.selectedColorFinal = alpha(new Color(Color.HSBtoRGB(this.color[0], this.color[1], this.color[2])), this.color[3]);
            Gui.drawRect(selectedX, selectedY, selectedX + selectedWidth, selectedY + selectedHeight, this.selectedColorFinal);

            {
                final int cursorX = (int) (pickerX + color[1]*pickerWidth);
                final int cursorY = (int) ((pickerY + pickerHeight) - color[2]*pickerHeight);
                Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
            }

            value = new Colour(selectedColorFinal);
        }

        if(listener != null) listener.onValueChange(value);
    }

    final int alpha(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha).getRGB();
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + preferredHeight;
    }

    protected boolean check(int minX, int minY, int maxX, int maxY, int curX, int curY) {
        return curX >= minX && curY >= minY && curX < maxX && curY < maxY;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0) {
            updateHovered(x, y, offscreen);

            if(hovered) {
                opened = !opened;
                updateWidth();
                updateHeight();
                return true;
            }
            if(opened) {
                pickingColor = check(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX / 2, mouseY / 2);
                pickingHue = check(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX / 2, mouseY / 2);
                pickingAlpha = check(alphaSliderX, alphaSliderY, alphaSliderX + alphaSliderWidth, alphaSliderY + alphaSliderHeight, mouseX / 2, mouseY / 2);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(int button, int x, int y, boolean offscreen) {
        pickingColor = pickingHue = pickingAlpha = false;
        return true;
    }

    public Colour getValue() {
        return value;
    }

    public void setValue(Colour value) {
        this.value = value;
        this.color = new float[] {value.r1, value.g1, value.b1, value.a1};
    }

    public void setListener(ValueChangeListener<Colour> listener) {
        this.listener = listener;
    }

    private void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            Gui.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height/6), x + width, y + (step+1) * (height/6), previousStep, nextStep);
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
}
