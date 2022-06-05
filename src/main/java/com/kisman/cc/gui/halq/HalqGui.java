package com.kisman.cc.gui.halq;

import com.kisman.cc.Kisman;
import com.kisman.cc.gui.MainGui;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.GuiModule;
import com.kisman.cc.gui.api.Component;
import com.kisman.cc.gui.particle.ParticleSystem;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author made by _kisman_ for Halq with love <3
 */
public class HalqGui extends GuiScreen {
    //variables for main gui settings
    public static LocateMode stringLocateMode = LocateMode.Left;
    public static Colour primaryColor = new Colour(Color.RED);
    public static Colour backgroundColor = new Colour(30, 30, 30, 121);
    public static boolean background = true, line = true, shadow = true, shadowCheckBox = false, test = true, shadowRects = false, test2 = true;
    public static int diff = 0, offsets = 0;

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

    public HalqGui(boolean notFullInit) {
        this.particleSystem = new ParticleSystem();
    }

    public HalqGui() {
        this(true);
        int offsetX = headerOffset - 1 - 1 - 1;
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
        test = GuiModule.instance.test.getValBoolean();
        shadowRects = GuiModule.instance.shadowRects.getValBoolean();
        line = GuiModule.instance.line.getValBoolean();
        diff = Config.instance.guiGradientDiff.getValInt();
        offsets = GuiModule.instance.offsets.getValInt();
        stringLocateMode = (LocateMode) GuiModule.instance.uwu.getValEnum();
        test2 = GuiModule.instance.test2.getValBoolean();

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
                CustomFontUtil.drawStringWithShadow(text, x + 5, y + (double) height / 2 - (double) CustomFontUtil.getFontHeight() / 2, -1);
                break;
        }
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

    public enum LocateMode {
        Center, Left
    }
}
