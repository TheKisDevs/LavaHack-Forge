package com.kisman.cc.gui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.KismanGuiScreen;
import com.kisman.cc.gui.MainGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.gui.particle.ParticleSystem;
import com.kisman.cc.gui.selectionbar.SelectionBar;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author made by _kisman_ for Halq with love <3
 */
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class HalqGui extends KismanGuiScreen {
    public SearchBar searchBar = new SearchBar();

    //variables for main gui settings
    public static LocateMode stringLocateMode = LocateMode.Left;
    public static Colour primaryColor = new Colour(Color.RED);
    public static Colour backgroundColor = new Colour(30, 30, 30, 121);
    public static Colour outlineColor = new Colour(Color.BLACK);
    public static Colour test2Color = new Colour(30, 30, 30, 121);
    public static Colour moduleHoverOverlay = new Colour(255, 255, 255, 60);
    public static boolean background = true,
            line = true,
            shadow = true,
            test = true,
            shadowRects = false,
            test2 = true,
            //Outline fields:
            componentsOutline = false,
            outlineTest = true,
            outlineTest2 = true,
            outlineHeaders = false;
    public static int diff = 0,
            textOffsetX = 5;
    public static double offsetsX = 0,
            offsetsY = 0,
            lineWidth = 1.0;
    //constants
    public static int height = 13;
    public static final int headerOffset = 5;
    public static final int width = 120;

    //frames list
    public final ArrayList<Frame> frames = new ArrayList<>();

    //particles
    public ParticleSystem particleSystem;

    /**
     * {@link com.kisman.cc.gui.mainmenu.gui.KismanMainMenuGui}
     */
    private GuiScreen lastGui = null;

    public HalqGui(GuiScreen lastGui) {
        this();
        this.lastGui = lastGui;
    }

    public HalqGui setLastGui(GuiScreen gui) {
        this.lastGui = gui;
        return this;
    }

    public HalqGui(@SuppressWarnings("unused") boolean notFullInit) {
        this.particleSystem = new ParticleSystem();
    }

    public HalqGui() {
        this(true);
        int offsetX = 0;
        for(Category cat : Category.values()) {
            frames.add(new Frame(cat, offsetX, 17));
            offsetX += headerOffset * 2 + width - 1 - 1 - 1 - 1 - 1 - 1 - 1;
        }
    }

    public void init() {
        SelectionBar.Guis.ClickGui.getOpen0().invoke();
        Kisman.instance.selectionBar.setReinit(true);
    }

    @Override
    public void initGui() {
        super.initGui();
        particleSystem = new ParticleSystem();
    }

    protected SelectionBar.Guis gui() {
        return SelectionBar.Guis.ClickGui;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(Kisman.instance.selectionBar.getSelection() != gui()) {
            MainGui.Companion.openGui(Kisman.instance.selectionBar);
            return;
        }

        primaryColor = GuiModule.instance.primaryColor.getColour();
        background = GuiModule.instance.background.getValBoolean();
        height = GuiModule.instance.buttonHeight.getValInt();
        shadow = GuiModule.instance.shadow.getValBoolean();
        test = GuiModule.instance.horizontalLines.getValBoolean();
        shadowRects = GuiModule.instance.shadowRects.getValBoolean();
        line = GuiModule.instance.verticalLines.getValBoolean();
        diff = Config.instance.guiGradientDiff.getValInt();
        offsetsX = GuiModule.instance.offsetsX.getValDouble();
        offsetsY = GuiModule.instance.offsetsY.getValDouble();
        stringLocateMode = (LocateMode) GuiModule.instance.uwu.getValEnum();
        test2 = GuiModule.instance.test2.getValBoolean();
        test2Color = GuiModule.instance.test2Color.getColour();
        textOffsetX = GuiModule.instance.textOffsetX.getValInt();
        outlineColor = GuiModule.instance.outlineColor.getColour();
        outlineTest = GuiModule.instance.outlineTest.getValBoolean();
        outlineTest2 = GuiModule.instance.outlineTest2.getValBoolean();
        outlineHeaders = GuiModule.instance.outlineHeaders.getValBoolean();
        componentsOutline = GuiModule.instance.componentsOutline.getValBoolean();
        lineWidth = GuiModule.instance.lineWidth.getValDouble();
        moduleHoverOverlay = GuiModule.instance.moduleHoverColor.getColour();

        if(!background) backgroundColor = new Colour(0, 0, 0, 0);
        else backgroundColor = GuiModule.instance.backgroundColor.getColour();

        drawDefaultBackground();

        if(Config.instance.guiParticles.getValBoolean()) {
            particleSystem.tick(10);
            particleSystem.render();
            particleSystem.onUpdate();
        }

//
        Kisman.instance.guiGradient.drawScreen(mouseX, mouseY);

        scrollWheelCheck();

        for(Frame frame : frames) {
            if(frame.reloading) continue;
            frame.render(mouseX, mouseY);
            if(frame.open) for(Component comp : frame.components) if(comp.visible()) {
                comp.updateComponent(frame.x, frame.y);
                comp.drawScreen(mouseX, mouseY);
            }
            frame.renderPost(mouseX, mouseY);
            frame.refresh();
        }

        for(Frame frame : frames) if(!frame.reloading) {
            frame.veryRenderPost(mouseX, mouseY);
        }

        drawSelectionBar(mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)/* throws IOException*/ {
        if(keyCode == 1) mc.displayGuiScreen(lastGui == null ? null : lastGui);
        super.keyTyped(typedChar, keyCode);
        for(Frame frame : frames) if(frame.open && keyCode != 1 && !frame.components.isEmpty() && !frame.reloading) for(Component mod : frame.components) if(mod.visible()) mod.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)/* throws IOException*/ {
//        if(!Kisman.instance.selectionBar.mouseClicked(mouseX, mouseY)) return;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for(Frame frame : frames) {
            if(frame.reloading) continue;
            if(frame.isMouseOnButton(mouseX, mouseY)) {
                if(mouseButton == 0) {
                    frame.dragging = true;
                    frame.dragX = mouseX - frame.x;
                    frame.dragY = mouseY - frame.y;
                }
                else if(mouseButton == 1) frame.open = !frame.open;
            }
            if(frame.open && !frame.components.isEmpty()) for(Component mod : frame.components) if(mod.visible()) mod.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for(Frame frame : frames) {
            if(frame.reloading) continue;
            frame.dragging = false;
            if(frame.open && !frame.components.isEmpty()) for(Component mod : frame.components) if(mod.visible()) mod.mouseReleased(mouseX, mouseY, state);
        }
    }

    @Override
    public void onGuiClosed() {
        try {if(mc.player != null && mc.world != null) mc.entityRenderer.getShaderGroup().deleteShaderGroup();} catch (Exception ignored) {}
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }

    public static void drawString(String text, int x, int y, int width, int height) {
        switch (stringLocateMode) {
            case Center:
                CustomFontUtil.drawCenteredStringWithShadow(text, x + (double) width / 2, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
                break;
            case Left:
                CustomFontUtil.drawStringWithShadow(text, x + textOffsetX, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
                break;
        }
    }

    public static void drawSuffix(String suffix, String parentText, double x, double y, double width, double height, Colour colour, int step) {
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 1);

        switch (stringLocateMode) {
            case Center:
                switch(step) {
                    case 1 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (float) y * 2, colour.getRGB());
                        break;
                    case 2 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (float) (y + (height / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f / 2)) * 2f, colour.getRGB());
                        break;
                    case 3 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (float) (y + height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, colour.getRGB());
                        break;
                }
                break;
            case Left:
                switch(step) {
                    case 1 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (float) y * 2, colour.getRGB());
                        break;
                    case 2 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (float) (y + (height / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f / 2f)) * 2f, colour.getRGB());
                        break;
                    case 3 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (float) (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (float) (y + height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, colour.getRGB());
                        break;
                }
                break;
        }

        GL11.glPopMatrix();
    }

    public static void drawSuffix(String suffix, String parentText, double x, double y, double width, double height, int count, int step) {
        drawSuffix(
                suffix,
                parentText,
                x,
                y,
                width,
                height,
                HalqGui.getGradientColour(count),
                step
        );
    }

    public static void drawCenteredString(String text, int x, int y, int width, int height) {
        CustomFontUtil.drawCenteredStringWithShadow(text, x + (double) width / 2, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
    }

    public static Colour getGradientColour(int count) {
        switch(Config.instance.guiGradient.getValString()) {
            case "None": return primaryColor;
            case "Rainbow": return new Colour(ColorUtils.rainbow(count * diff, 1, 1));
            case "Astolfo": return new Colour(ColorUtils.getAstolfoRainbow(count * diff));
            case "Pulsive": return ColorUtils.twoColorEffect(primaryColor, primaryColor.setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * diff) / 60.0);
        }
        return primaryColor;
    }

    private void scrollWheelCheck() {
        int dWheel = Mouse.getDWheel();
        if(dWheel < 0) for(Frame frame : frames) {
            if(frame.reloading) continue;
            if(Config.instance.horizontalScroll.getValBoolean() && Keyboard.getEventKeyState() && Keyboard.getEventKey() == Config.instance.keyForHorizontalScroll.getKey()) frame.x = frame.x - (int) Config.instance.scrollSpeed.getValDouble();
            else frame.y = frame.y - (int) Config.instance.scrollSpeed.getValDouble();
        } else if(dWheel > 0) for(Frame frame : frames) {
            if(frame.reloading) continue;
            if(Config.instance.horizontalScroll.getValBoolean() && Keyboard.getEventKeyState() && Keyboard.getEventKey() == Config.instance.keyForHorizontalScroll.getKey()) frame.x = frame.x + (int) Config.instance.scrollSpeed.getValDouble();
            else frame.y = frame.y + (int) Config.instance.scrollSpeed.getValDouble();
        }
    }

    public static void renderComponent(
            Component component
    ) {
        if(!component.visible()) return;

        if(HalqGui.line) {
            Render2DUtil.drawRectWH(
                    component.getX(),
                    component.getY(),
                    1,
                    component.getRawHeight(),
                    HalqGui.getGradientColour(component.getCount()).getRGB()
            );
            Render2DUtil.drawRectWH(
                    component.getX() + LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width) - 1,
                    component.getY(),
                    1,
                    component.getRawHeight(),
                    HalqGui.getGradientColour(component.getCount()).getRGB()
            );
        }

        if(component instanceof Openable) {
            Openable openable = (Openable) component;

            if(HalqGui.test && openable.isOpen() && !openable.getComponents().isEmpty()) {
                Render2DUtil.drawRectWH(
                        component.getX(),
                        component.getY() + component.getRawHeight(),
                        LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width),
                        1,
                        HalqGui.getGradientColour(openable.getComponents().get(0).getCount()).getRGB()
                );

                int height = doIterationUpdateComponent(
                        openable.getComponents(),
                        0
                );

                Render2DUtil.drawRectWH(
                        component.getX(),
                        component.getY() + component.getRawHeight() + height - 1,
                        LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width),
                        1,
                        HalqGui.getGradientColour(openable.getComponents().get(openable.getComponents().size() - 1).getCount()).getRGB()
                );
            }
        }
    }

    public static int doIterationUpdateComponent(
            ArrayList<Component> components,
            int height
    ) {
        for(Component component : components) {
            if(!component.visible()) continue;
            height += component.getHeight();
            if(component instanceof Openable) {
                Openable openable = (Openable) component;
                if(openable.isOpen()) height = doIterationUpdateComponent(openable.getComponents(), height);
            }
        }

        return height;
    }

    public static void drawComponentOutline(Component component, boolean offsetsCheck, boolean drawSecondLines, boolean full) {
        if(componentsOutline) {
            int width0 = LayerControllerKt.getModifiedWidth(component.getLayer(), width);

            if(!full && (offsetsX > 0 || offsetsY > 0 || !offsetsCheck)) {
                double width1 = width0 - (offsetsX * 2);

                Render2DUtil.drawRectWH(//h
                        component.getX() + offsetsX,
                        component.getY() + offsetsY,
                        width1,
                        lineWidth,
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//h
                        component.getX() + offsetsX,
                        component.getY() + offsetsY + component.getRawHeight() - (offsetsY * 2) - lineWidth,
                        width1,
                        lineWidth,
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//v
                        component.getX() + offsetsX,
                        component.getY() + offsetsY,
                        lineWidth,
                        component.getRawHeight() - (offsetsY * 2),
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//v
                        component.getX() + offsetsX + width1 - lineWidth,
                        component.getY() + offsetsY,
                        lineWidth,
                        component.getRawHeight() - (offsetsY * 2),
                        outlineColor.getRGB()
                );
            }

            if(drawSecondLines) {
                if(full) {
                    Render2DUtil.drawRectWH(//h
                            component.getX(),
                            component.getY(),
                            width0,
                            lineWidth,
                            outlineColor.getRGB()
                    );
                    Render2DUtil.drawRectWH(//h
                            component.getX(),
                            component.getY() + component.getRawHeight() - lineWidth,
                            width0,
                            lineWidth,
                            outlineColor.getRGB()
                    );
                }

                Render2DUtil.drawRectWH(//v
                        component.getX(),
                        component.getY(),
                        lineWidth,
                        component.getRawHeight(),
                        outlineColor.getRGB()
                );
                Render2DUtil.drawRectWH(//v
                        component.getX() + width0 - lineWidth,
                        component.getY(),
                        lineWidth,
                        component.getRawHeight(),
                        outlineColor.getRGB()
                );
            }
        }
    }

    @Deprecated
    public static void drawComponentOverlay(Component component, int mouseX, int mouseY){
        double x = component.getX() + HalqGui.offsetsX;
        double y = component.getY() + HalqGui.offsetsY;
        double width = LayerControllerKt.getModifiedWidth(component.getLayer(), HalqGui.width) - HalqGui.offsetsX * 2;
        double height = component.getHeight() - HalqGui.offsetsX * 2;
        if(mouseX <= component.getX() || mouseX >= x + width || mouseY <= y || mouseY >= y + height)
            return;
        Render2DUtil.drawRectWH(x, y, width, height, moduleHoverOverlay.getRGB());
    }

    public static boolean visible(String name) {
        return Kisman.instance.halqGui.searchBar.text().isEmpty() || name.toLowerCase().contains(Kisman.instance.halqGui.searchBar.text().toLowerCase());
    }

    public static boolean visible(Openable openable) {
        if(Kisman.instance.halqGui.searchBar.text().isEmpty()) return true;

        for(Component component : openable.getComponents()) {
            if(component.visible()) return true;
        }

        return false;
    }

    public enum LocateMode {
        Center, Left
    }
}
