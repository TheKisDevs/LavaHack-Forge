package com.kisman.cc.gui.halq.components.sub;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.SettingComponent;
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.objects.screen.AbstractGradient;
import com.kisman.cc.util.render.objects.screen.Icons;
import com.kisman.cc.util.render.objects.screen.Vec4d;
import net.minecraft.client.gui.Gui;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static com.kisman.cc.util.render.Render2DUtil.drawGradientRect;
import static com.kisman.cc.util.render.Render2DUtil.drawLeftGradientRect;

@SuppressWarnings("SuspiciousNameCombination")
public class ColorButton extends ShaderableImplementation implements Component, SettingComponent {
    private final Setting setting;
    private Colour color;
    private int pickerWidth, height;
    public boolean open = false;
    private boolean baseHover, hueHover, alphaHover, pickingBase, pickingHue, pickingAlpha;
    private Colour clearColor;
    private boolean doCopy = true;
    private boolean doPaste = true;

    public ColorButton(Setting setting, int x, int y, int offset, int count, int layer) {
        super(x, y, count, offset, layer);
        this.setting = setting;
        this.color = setting.getColour();
        this.pickerWidth = getWidth() - HalqGui.height;
        setClearColor(setting.getColour());
    }

    private void setClearColor(Colour colour){
        Colour c = new Colour();
        c.setHue(Color.RGBtoHSB(colour.r, colour.g, colour.b, null)[0]);
        c.setSaturation(1.0f);
        c.setBrightness(1.0f);
        this.clearColor = c;
    }

    private void copyColorToClipboard(){
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(getColorHex()), null);
    }

    private String getColorHex(){
        return Integer.toHexString((color.r << 24) | (color.g << 16) | (color.b << 8) | color.a);
    }

    private Colour readColorHex(String hex){
        int color;
        try {
            color = parseInt(hex, 16);
        } catch (NumberFormatException e){
            Kisman.LOGGER.error("[ColorButton] Could not read hex: " + hex, e);
            return null;
        }
        return new Colour(((color & 0xff) << 24) | (color >> 8));
    }

    /**
     * @author Cubic
     */
    private static int parseInt(String hex, int radix) throws NumberFormatException {
        int r = 0;
        int i = 0;
        if(hex.equals(""))
            throw new NumberFormatException();
        boolean negative = false;
        char first = hex.charAt(0);
        if(first == '-') {
            negative = true;
            i++;
        } else if(first == '+')
            i++;
        while (i < hex.length()){
            int c = digit(hex.charAt(i++), radix);
            r *= radix;
            r += c;
        }
        return negative ? -r : r;
    }

    /**
     * @author Cubic
     */
    private static int digit(char ch, int radix) throws NumberFormatException {
        int c = ch < 65 || ch > 90 ? ch : ch - 32;
        int r = c < 58 ? c - 48 : c - 87;
        if(r < 0 || r >= radix)
            throw new NumberFormatException();
        return r;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(!setting.getColour().equals(color)){
            color = setting.getColour();
            setClearColor(color);
        }
        this.pickerWidth = getWidth();
        normalRender = () -> {
            Render2DUtil.drawRectWH(getX(), getY(), getWidth(), getHeight(), HalqGui.backgroundColor.getRGB());
            if (HalqGui.shadow) {
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() / 2f, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() / 2f, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{getX() + HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(color.getColor(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
                Render2DUtil.drawAbstract(
                        new AbstractGradient(
                                new Vec4d(
                                        new double[]{getX() + getWidth() / 2f, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() - HalqGui.offsetsX, getY() + HalqGui.height - HalqGui.offsetsY},
                                        new double[]{getX() + getWidth() / 2f, getY() + HalqGui.height - HalqGui.offsetsY}
                                ),
                                ColorUtils.injectAlpha(HalqGui.backgroundColor.getRGB(), GuiModule.instance.minPrimaryAlpha.getValInt()),
                                ColorUtils.injectAlpha(color.getColor(), GuiModule.instance.minPrimaryAlpha.getValInt())
                        )
                );
            } else Render2DUtil.drawRectWH(getX() + HalqGui.offsetsX, getY() + HalqGui.offsetsY, getWidth() - HalqGui.offsetsX * 2, getHeight() - HalqGui.offsetsY * 2, color.getRGB());

            HalqGui.drawString(setting.getTitle(), getX(), getY(), getWidth(), HalqGui.height);

            if (open) {
                int offsetY = HalqGui.height;

                Colour renderColor = GuiModule.instance.colorPickerClearColor.getValBoolean() ? clearColor : color;

                drawPickerBase(getX() + HalqGui.offsetsX, getY() + offsetY + HalqGui.offsetsY, pickerWidth - (HalqGui.offsetsX * 2), pickerWidth - (HalqGui.offsetsY * 2), renderColor.r1, renderColor.g1, renderColor.b1, renderColor.a1, mouseX, mouseY);
                offsetY += pickerWidth;

                drawHueSlider(getX() + HalqGui.offsetsX, getY() + offsetY + HalqGui.offsetsY, pickerWidth - (HalqGui.offsetsX * 2), HalqGui.height - 3 - (HalqGui.offsetsY * 2), color.getHue(), mouseX, mouseY);
                offsetY += HalqGui.height - 3;

                drawAlphaSlider(getX() + HalqGui.offsetsX, getY() + offsetY + HalqGui.offsetsY, pickerWidth - (HalqGui.offsetsX * 2), HalqGui.height - 3 - (HalqGui.offsetsY * 2), color.r1, color.g1, color.b1, color.a1, mouseX, mouseY);
                height = offsetY + HalqGui.height - 3;

                updateValue(mouseX, mouseY, getX() + HalqGui.offsetsX, getY() + HalqGui.height + HalqGui.offsetsY);

                {
                    final int cursorX = (int) (getX() + color.RGBtoHSB()[1] * pickerWidth);
                    final int cursorY = (int) ((getY() + HalqGui.height + 5 + pickerWidth) - color.RGBtoHSB()[2] * pickerWidth);

                    if (GuiModule.instance.colorPickerExtra.getValBoolean() && Mouse.isButtonDown(0) && pickingBase) {
                        Gui.drawRect(cursorX - 8, cursorY - 8, cursorX + 8, cursorY + 8, new Color(0, 0, 0, 255).getRGB());
                        Gui.drawRect(cursorX - 7, cursorY - 7, cursorX + 7, cursorY + 7, color.getRGB());
                        Icons.COLOR_PICKER.render(cursorX, cursorY - 18, 16.0, 16.0);
                    } else {
                        Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);
                    }
                }
            }
        };

        setting.setColour(color);
    }

    private void updateValue(int mouseX, int mouseY, double x, double y) {
        float restrictedX = (float) Math.min(Math.max(x, mouseX), x + pickerWidth);
        float restrictedY = (float) Math.min(Math.max(y, mouseY), y + pickerWidth);

        if (pickingBase) {
            color.setSaturation((restrictedX - (float) x) / pickerWidth);
            color.setBrightness(1 - (restrictedY - (float) y) / pickerWidth);
        }

        if (pickingHue) {
            color.setHue((restrictedX - (float) x) / pickerWidth);
            clearColor.setHue((restrictedX - (float) x) / pickerWidth);
        }

        if (pickingAlpha) color.setAlpha(1 - (restrictedX - (float) x) / pickerWidth);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(button == 0) {
            if(isMouseOnButton2(mouseX, mouseY)) open = !open;
            pickingBase = baseHover;
            pickingHue = hueHover;
            pickingAlpha = alphaHover;
        }

        if(!GuiModule.instance.colorPickerCopyPaste.getValBoolean()) return;

        if(button == 1 && baseHover && doCopy){
            copyColorToClipboard();
            doCopy = false;
        }

        if(button == 1 && !baseHover) doCopy = true;

        if(button == 2 && baseHover && doPaste){
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            String content;

            if(!transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) return;

            try {
                content = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException e) {
                Kisman.LOGGER.error(e);
                return;
            }

            Colour color = readColorHex(content);

            if(color == null) return;

            this.color = color;
            setClearColor(this.color);
            doPaste = false;
        }

        if(button == 2 && !baseHover) doPaste = true;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        pickingBase = pickingAlpha = pickingHue = false;
        if(mouseButton == 1) doCopy = true;
        else if(mouseButton == 2) doPaste = true;
    }

    @Override
    public int getHeight() {
        return HalqGui.height + (open ? height : 0);
    }

    @Override
    public int getRawHeight() {
        return HalqGui.height + (open ? height : 0);
    }

    @Override
    public boolean visible() {
        return setting.isVisible() && HalqGui.visible(setting.getTitle());
    }

    public boolean isMouseOnButton2(int x, int y) {
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + HalqGui.height;
    }

    private void drawHueSlider(double x, double y, double width, double height, float hue, int mouseX, int mouseY) {
        hueHover = mouseX > x && mouseX < x + width && mouseY > y && mouseY <  y + height;
        int step = 0;
        if (height > width) {
            Render2DUtil.drawRect(x, y, x + width, y + 4, -1);
            y += 4;
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height/6), x + width, y + (step+1) * (height/6), previousStep, nextStep);
                step++;
            }
            final int sliderMinY = (int) (y + (height*hue)) - 4;
            Render2DUtil.drawRect(x, sliderMinY - 1, x+width, sliderMinY + 1, -1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step/6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step+1)/6, 1.0f, 1.0f);
                this.gradient(x + step * (width/6), y, x + (step+1) * (width/6), y + height, previousStep, nextStep, true);
                step++;
            }
            double sliderMinX = x + (width * hue);
            Render2DUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        }
    }

    public void drawAlphaSlider(double x, double y, double width, double height, float red, float green, float blue, float alpha, int mouseX, int mouseY) {
        alphaHover = mouseX > x && mouseX < x + width && mouseY > y && mouseY <  y + height;
        boolean left = true;
        double checkerBoardSquareSize = height / 2;

        for (double squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                Render2DUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, 0xFFFFFFFF);
                Render2DUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, 0xFF909090);

                if (squareIndex < width - checkerBoardSquareSize) {
                    double minX = x + squareIndex + checkerBoardSquareSize;
                    double maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    Render2DUtil.drawRect(minX, y, maxX, y + height, 0xFF909090);
                    Render2DUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, 0xFFFFFFFF);
                }
            }

            left = !left;
        }

        drawLeftGradientRect(x, y, x + width, y + height, new Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        Render2DUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
    }

    private void drawPickerBase(double pickerX, double pickerY, double pickerWidth, double pickerHeight, float red, float green, float blue, float alpha, int mouseX, int mouseY) {
        baseHover = mouseX > pickerX && mouseX < pickerX + pickerWidth && mouseY > pickerY && mouseY <  pickerY + pickerHeight;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glVertex2d(pickerX, pickerY);
            GL11.glVertex2d(pickerX, pickerY + pickerHeight);
            GL11.glColor4f(red, green, blue, alpha);
            GL11.glVertex2d(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glVertex2d(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2d(pickerX, pickerY);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glVertex2d(pickerX, pickerY + pickerHeight);
            GL11.glVertex2d(pickerX + pickerWidth, pickerY + pickerHeight);
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glVertex2d(pickerX + pickerWidth, pickerY);
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void gradient(double minX, double minY, double maxX, double maxY, int startColor, int endColor, boolean left) {
        if (left) {
            final float startA = (startColor >> 24 & 0xFF) / 255.0f;
            final float startR = (startColor >> 16 & 0xFF) / 255.0f;
            final float startG= (startColor >> 8 & 0xFF) / 255.0f;
            final float startB = (startColor & 0xFF) / 255.0f;

            final float endA = (endColor >> 24 & 0xFF) / 255.0f;
            final float endR = (endColor >> 16 & 0xFF) / 255.0f;
            final float endG = (endColor >> 8 & 0xFF) / 255.0f;
            final float endB = (endColor & 0xFF) / 255.0f;

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2d(minX, minY);
                GL11.glVertex2d(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2d(maxX, maxY);
                GL11.glVertex2d(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } else drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }

    @NotNull
    @Override
    public Setting setting() {
        return setting;
    }
}