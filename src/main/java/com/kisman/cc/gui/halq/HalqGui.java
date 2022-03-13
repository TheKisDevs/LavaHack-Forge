package com.kisman.cc.gui.halq;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.module.client.HalqGuiModule;
import com.kisman.cc.gui.halq.component.Component;
import com.kisman.cc.gui.particle.ParticleSystem;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
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
    public static Color primaryColor = Color.RED;
    public static Color backgroundColor = new Color(30, 30, 30, 121);
    public static boolean background = true, line = true, shadow = true, shadowCheckBox = false, test = true;
    public static int diff = 0;
    public static float testLight; /**@range 0-1*/

    //constants
    public static final int height = 13;
    public static final int headerOffset = 5;
    public static final int width = 100;

    //frames list
    private final ArrayList<Frame> frames = new ArrayList<>();

    //particles
    private final ParticleSystem particleSystem;

    /**
     *@link {com.kisman.cc.gui.mainmenu.gui.KismanMainMenuGui}
     */
    private GuiScreen lastGui = null;

    public HalqGui(GuiScreen lastGui) {
        this();
        this.lastGui = lastGui;
    }

    public HalqGui() {
        this.particleSystem = new ParticleSystem(300);
        int offsetX = 5 + headerOffset;
        for(Category cat : Category.values()) {
            frames.add(new Frame(cat, offsetX, 10));
            offsetX += headerOffset * 5 + 5 + width;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        background = HalqGuiModule.instance.background.getValBoolean();
        shadowCheckBox = HalqGuiModule.instance.shadow.getValBoolean();
        test = HalqGuiModule.instance.test.getValBoolean();
        diff = Config.instance.guiGradientDiff.getValInt();

        if(!background) backgroundColor = new Color(0, 0, 0, 0);
        else backgroundColor = new Color(30, 30, 30, 121);

        drawDefaultBackground();

        scrollWheelCheck();
        for(Frame frame : frames) {
            frame.render(mouseX, mouseY);
            if(frame.open) for(Component comp : frame.mods) {
                comp.updateComponent(frame.x, frame.y);
                comp.drawScreen(mouseX, mouseY);
            }
            frame.renderPost();
            frame.refresh();
        }

        if(Config.instance.guiParticles.getValBoolean()) {
            particleSystem.tick(10);
            particleSystem.render();
            particleSystem.onUpdate();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) mc.displayGuiScreen(lastGui == null ? null : lastGui);
        for(Frame frame : frames) if(frame.open && keyCode != 1 && !frame.mods.isEmpty()) for(Component b : frame.mods) b.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Frame frame : frames) {
            if(frame.isMouseOnButton(mouseX, mouseY)) {
                if(mouseButton == 0) {
                    frame.dragging = true;
                    frame.dragX = mouseX - frame.x;
                    frame.dragY = mouseY - frame.y;
                }
                else if(mouseButton == 1) frame.open = !frame.open;
            }
            if(frame.open && !frame.mods.isEmpty()) for(Component mod : frame.mods) mod.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(Frame frame : frames) {
            frame.dragging = false;
            if(frame.open && !frame.mods.isEmpty()) for(Component mod : frame.mods) mod.mouseReleased(mouseX, mouseY, state);
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
                CustomFontUtil.drawCenteredStringWithShadow(text, x + width / 2, y + height / 2 - CustomFontUtil.getFontHeight() / 2, -1);
                break;
            case Left:
                CustomFontUtil.drawStringWithShadow(text, x + 5, y + height / 2 - CustomFontUtil.getFontHeight() / 2, -1);
                break;
        }
    }

    public static void drawCenteredString(String text, int x, int y, int width, int height) {
        CustomFontUtil.drawCenteredStringWithShadow(text, x + width / 2, y + height / 2 - CustomFontUtil.getFontHeight() / 2, -1);
    }

    public static Colour getGradientColour(int count) {
        switch(Config.instance.guiGradient.getValString()) {
            case "None": return new Colour(primaryColor);
            case "Rainbow": return new Colour(ColorUtils.rainbow(count * diff, 1, 1));
            case "Astolfo": return new Colour(ColorUtils.getAstolfoRainbow(count * diff));
            case "Pulsive": return ColorUtils.twoColorEffect(new Colour(primaryColor), (new Colour(primaryColor)).setBrightness(0.25f), Math.abs(System.currentTimeMillis() / 10L) / 100.0 + 6.0 * (count * diff) / 60.0);
        }
        return new Colour(primaryColor);
    }

    private void scrollWheelCheck() {
        int dWheel = Mouse.getDWheel();
        if(dWheel < 0) for(Frame frame : frames) {
            if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == Config.instance.keyForHorizontalScroll.getKey()) frame.x = frame.x - (int) Config.instance.scrollSpeed.getValDouble();
            else frame.y = frame.y - (int) Config.instance.scrollSpeed.getValDouble();
        } else if(dWheel > 0) for(Frame frame : frames) {
            if(Keyboard.getEventKeyState() && Keyboard.getEventKey() == Config.instance.keyForHorizontalScroll.getKey()) frame.x = frame.x + (int) Config.instance.scrollSpeed.getValDouble();
            else frame.y = frame.y + (int) Config.instance.scrollSpeed.getValDouble();
        }
    }

    public enum LocateMode {
        Center, Left
    }
}