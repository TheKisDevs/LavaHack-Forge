package com.kisman.cc.gui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.MainGui;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.api.Openable;
import com.kisman.cc.gui.halq.util.LayerControllerKt;
import com.kisman.cc.gui.particle.ParticleSystem;
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
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author made by _kisman_ for Halq with love <3
 */
@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class HalqGui extends GuiScreen {
    //variables for main gui settings
    public static LocateMode stringLocateMode = LocateMode.Left;
    public static Colour primaryColor = new Colour(Color.RED);
    public static Colour backgroundColor = new Colour(30, 30, 30, 121);
    public static boolean background = true, line = true, shadow = true, shadowCheckBox = false, test = true, shadowRects = false, test2 = true;
    public static int diff = 0, offsets = 0, textOffsetX = 5;

    //constants
    public static final int height = 13;
    public static final int headerOffset = 5;
    public static final int width = 100;

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
            offsetX += headerOffset * 2 + width - 1 - 1 - 1 - 1 - 1 - 1 - 1 - 1 - 1 - 1;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        particleSystem = new ParticleSystem();
    }

    protected MainGui.Guis gui() {
        return MainGui.Guis.ClickGui;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(Kisman.instance.selectionBar.getSelection() != gui()) {
            MainGui.Companion.openGui(Kisman.instance.selectionBar);
            return;
        }

        primaryColor = GuiModule.instance.primaryColor.getColour();
        background = GuiModule.instance.background.getValBoolean();
        shadowCheckBox = GuiModule.instance.shadow.getValBoolean();
        test = GuiModule.instance.horizontalLines.getValBoolean();
        shadowRects = GuiModule.instance.shadowRects.getValBoolean();
        line = GuiModule.instance.verticalLines.getValBoolean();
        diff = Config.instance.guiGradientDiff.getValInt();
        offsets = GuiModule.instance.offsets.getValInt();
        stringLocateMode = (LocateMode) GuiModule.instance.uwu.getValEnum();
        test2 = GuiModule.instance.test2.getValBoolean();
        textOffsetX = GuiModule.instance.textOffsetX.getValInt();

        if(!background) backgroundColor = new Colour(0, 0, 0, 0);
        else backgroundColor = GuiModule.instance.backgroundColor.getColour();

        drawDefaultBackground();

        if(Config.instance.guiParticles.getValBoolean()) {
            particleSystem.tick(10);
            particleSystem.render();
            particleSystem.onUpdate();
        }

        Kisman.instance.guiGradient.drawScreen(mouseX, mouseY);

        scrollWheelCheck();
        for(Frame frame : frames) {
            if(frame.reloading) continue;
            frame.render(mouseX, mouseY);
            if(frame.open) for(Component comp : frame.mods) if(comp.visible()) {
                comp.updateComponent(frame.x, frame.y);
                comp.drawScreen(mouseX, mouseY);
            }
            frame.renderPost(mouseX, mouseY);
            frame.refresh();
        }

        for(Frame frame : frames) if(!frame.reloading) {
            frame.veryRenderPost(mouseX, mouseY);
        }

        Kisman.instance.selectionBar.drawScreen(mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) mc.displayGuiScreen(lastGui == null ? null : lastGui);
        for(Frame frame : frames) if(frame.open && keyCode != 1 && !frame.mods.isEmpty() && !frame.reloading) for(Component mod : frame.mods) if(mod.visible()) mod.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(!Kisman.instance.selectionBar.mouseClicked(mouseX, mouseY)) return;
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
            if(frame.open && !frame.mods.isEmpty()) for(Component mod : frame.mods) if(mod.visible()) mod.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for(Frame frame : frames) {
            if(frame.reloading) continue;
            frame.dragging = false;
            if(frame.open && !frame.mods.isEmpty()) for(Component mod : frame.mods) if(mod.visible()) mod.mouseReleased(mouseX, mouseY, state);
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

    public static void drawSuffix(String suffix, String parentText, int x, int y, int width, int height, Colour colour, int step) {
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 1);

        switch (stringLocateMode) {
            case Center:
                switch(step) {
                    case 1 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), y * 2, colour.getRGB());
                        break;
                    case 2 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (y + (height / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f / 2)) * 2f, colour.getRGB());
                        break;
                    case 3 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, ((x + width / 2) + CustomFontUtil.getStringWidth(parentText)), (y + height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, colour.getRGB());
                        break;
                }
                break;
            case Left:
                switch(step) {
                    case 1 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, y * 2, colour.getRGB());
                        break;
                    case 2 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (y + (height / 2) - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f / 2f)) * 2f, colour.getRGB());
                        break;
                    case 3 :
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(suffix, (x + CustomFontUtil.getStringWidth(parentText) + textOffsetX) * 2, (y + height - (Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT / 2f)) * 2, colour.getRGB());
                        break;
                }
                break;
        }

        GL11.glPopMatrix();
    }

    public static void drawSuffix(String suffix, String parentText, int x, int y, int width, int height, int count, int step) {
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

    public enum LocateMode {
        Center, Left
    }
}
